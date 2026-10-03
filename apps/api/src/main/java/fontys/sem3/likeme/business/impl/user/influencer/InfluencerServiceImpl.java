package fontys.sem3.likeme.business.impl.user.influencer;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fontys.sem3.likeme.business.exception.security.setuptoken.InvalidSetupTokenException;
import fontys.sem3.likeme.business.exception.user.influencer.DuplicateInfluencerDataException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerNotFoundException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerServiceException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerSetupException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerDataException;
import fontys.sem3.likeme.business.interfaces.security.PasswordService;
import fontys.sem3.likeme.business.interfaces.security.setuptoken.SetupTokenService;
import fontys.sem3.likeme.business.interfaces.user.influencer.InfluencerService;
import fontys.sem3.likeme.business.validator.user.influencer.InfluencerApplicationValidator;
import fontys.sem3.likeme.business.validator.user.influencer.InfluencerValidator;
import fontys.sem3.likeme.domain.security.setuptoken.SetupToken;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import fontys.sem3.likeme.domain.user.influencer.InfluencerStatus;
import fontys.sem3.likeme.repository.interfaces.influencer.InfluencerApplicationRepository;
import fontys.sem3.likeme.repository.interfaces.influencer.InfluencerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InfluencerServiceImpl implements InfluencerService {
    private final InfluencerRepository influencerRepository;
    private final InfluencerApplicationRepository applicationRepository;
    private final SetupTokenService setupTokenService;
    private final InfluencerValidator influencerValidator;
    private final InfluencerApplicationValidator influencerApplicationValidator;
    private final PasswordService passwordService;

    @Override
    @Transactional
    public Influencer createPendingInfluencer(InfluencerApplication application) {
        if (application == null) {
            throw new InvalidInfluencerDataException("Influencer application cannot be null");
        }

        try {
            InfluencerApplication savedApplication = applicationRepository.save(application);

            Influencer influencer = Influencer.builder()
                    .application(savedApplication)
                    .status(InfluencerStatus.PENDING_SETUP)
                    .isInstagramConnected(false)
                    .build();

            influencerValidator.validateCreate(influencer);
            return influencerRepository.save(influencer);
        } catch (InvalidInfluencerDataException e) {
            throw e;
        } catch (Exception e) {
            throw new InfluencerServiceException("Failed to create pending influencer", e);
        }
    }

    @Override
    @Transactional
    public Influencer completeInfluencerSetup(String token, String password) {
        if (password == null || password.isEmpty()) {
            throw new InvalidInfluencerDataException("Password cannot be empty");
        }

        try {
            SetupToken setupToken = setupTokenService.validateToken(token);
            Influencer influencer = getInfluencerByApplicationId(setupToken.getApplication().getId());

            if (influencer.getStatus() != InfluencerStatus.PENDING_SETUP) {
                throw new InfluencerSetupException("Influencer must be in PENDING_SETUP state to complete setup");
            }

            String salt = passwordService.generateSalt();
            influencer.setSalt(salt);
            influencer.setPassword(passwordService.hashPassword(password, salt));
            influencer.setStatus(InfluencerStatus.PENDING_INSTAGRAM);

            influencerValidator.validateUpdate(influencer);
            setupTokenService.invalidateToken(token);

            return influencerRepository.save(influencer);
        } catch (InvalidSetupTokenException | InfluencerNotFoundException | InfluencerSetupException e) {
            throw e;
        } catch (Exception e) {
            throw new InfluencerServiceException("Failed to complete influencer setup", e);
        }
    }

    @Override
    public List<Influencer> getAllInfluencers() {
        try {
            return influencerRepository.findAll();
        } catch (Exception e) {
            throw new InfluencerServiceException("Failed to retrieve all influencers", e);
        }
    }

    @Override
    public Influencer getInfluencerById(Long id) {
        if (id == null) {
            throw new InvalidInfluencerDataException();
        }
        try {
            return influencerRepository.findById(id)
                    .orElseThrow(() -> new InfluencerNotFoundException("Influencer not found with id: " + id));
        } catch (InfluencerNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfluencerServiceException("Error retrieving influencer with id: " + id, e);
        }
    }

    @Override
    public Influencer getInfluencerByApplicationId(Long applicationId) {
        if (applicationId == null) {
            throw new InvalidInfluencerDataException("Application ID cannot be null");
        }
        try {
            return influencerRepository.findByApplicationId(applicationId)
                    .orElseThrow(() -> new InfluencerNotFoundException(
                            "Influencer not found for application: " + applicationId));
        } catch (InfluencerNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfluencerServiceException("Error retrieving influencer for application: " + applicationId, e);
        }
    }

    @Override
    @Transactional
    public Influencer createInfluencer(Influencer influencer) {
        if (influencer == null) {
            throw new InvalidInfluencerDataException("Influencer cannot be null");
        }

        try {
            String salt = passwordService.generateSalt();
            influencer.setSalt(salt);
            influencer.setPassword(passwordService.hashPassword(influencer.getPassword(), salt));
            influencer.getApplication().setIsApproved(true);

            influencerApplicationValidator.validateCreate(influencer.getApplication());
            InfluencerApplication savedApplication = applicationRepository.save(influencer.getApplication());

            influencer.setApplication(savedApplication);
            influencerValidator.validateCreate(influencer);

            return influencerRepository.save(influencer);
        } catch (InvalidInfluencerDataException | DuplicateInfluencerDataException e) {
            throw e;
        } catch (Exception e) {
            throw new InfluencerServiceException("Failed to create influencer", e);
        }
    }

    @Override
    @Transactional
    public Influencer updateInfluencer(Influencer influencer) {
        if (influencer == null) {
            throw new InvalidInfluencerDataException("Influencer data cannot be null");
        }

        try {
            Influencer existingInfluencer = getInfluencerById(influencer.getId());

            if (!existingInfluencer.getPassword().equals(influencer.getPassword())) {
                String salt = passwordService.generateSalt();
                influencer.setSalt(salt);
                influencer.setPassword(passwordService.hashPassword(influencer.getPassword(), salt));
            } else {
                influencer.setSalt(existingInfluencer.getSalt());
            }

            if (influencer.getApplication() != null) {
                InfluencerApplication updatedApplication = InfluencerApplication.builder()
                        .id(existingInfluencer.getApplication().getId())
                        .username(influencer.getApplication().getUsername())
                        .email(influencer.getApplication().getEmail())
                        .phoneNumber(influencer.getApplication().getPhoneNumber())
                        .about(influencer.getApplication().getAbout())
                        .instagramHandle(influencer.getApplication().getInstagramHandle())
                        .isApproved(influencer.getApplication().getIsApproved())
                        .billingDetails(influencer.getApplication().getBillingDetails())
                        .profilePhotoPath(influencer.getApplication().getProfilePhotoPath())
                        .coverPhotoPath(influencer.getApplication().getCoverPhotoPath())
                        .createdOn(existingInfluencer.getApplication().getCreatedOn())
                        .updatedOn(new Date())
                        .build();

                influencerValidator.validateUpdate(influencer);

                InfluencerApplication savedApplication = applicationRepository.save(updatedApplication);
                influencer = Influencer.builder()
                        .id(influencer.getId())
                        .application(savedApplication)
                        .password(influencer.getPassword())
                        .salt(influencer.getSalt())
                        .status(influencer.getStatus())
                        .isInstagramConnected(influencer.getIsInstagramConnected())
                        .instagramAccessToken(influencer.getInstagramAccessToken())
                        .isActive(influencer.getIsActive())
                        .createdOn(existingInfluencer.getCreatedOn())
                        .updatedOn(new Date())
                        .lastLoginOn(influencer.getLastLoginOn())
                        .build();
            }

            return influencerRepository.save(influencer);
        } catch (InfluencerNotFoundException | InvalidInfluencerDataException e) {
            throw e;
        } catch (Exception e) {
            throw new InfluencerServiceException("Failed to update influencer with id: " + influencer.getId(), e);
        }
    }

    @Override
    public void deleteInfluencer(Long id) {
        if (id == null) {
            throw new InvalidInfluencerDataException();
        }

        try {
            if (!influencerRepository.existsById(id)) {
                throw new InfluencerNotFoundException("Influencer not found with id: " + id);
            }
            influencerRepository.deleteById(id);
        } catch (InfluencerNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfluencerServiceException("Failed to delete influencer: " + id, e);
        }
    }

    @Override
    public boolean existsByApplicationId(Long applicationId) {
        if (applicationId == null) {
            throw new InvalidInfluencerDataException("Application ID cannot be null");
        }
        try {
            return influencerRepository.existsByApplicationId(applicationId);
        } catch (Exception e) {
            throw new InfluencerServiceException("Error checking existence for application: " + applicationId, e);
        }
    }

    @Override
    @Transactional
    public Influencer connectInstagram(Long id, String token) {
        if (id == null) {
            throw new InvalidInfluencerDataException();
        }
        if (token == null || token.isEmpty()) {
            throw new InvalidInfluencerDataException("Instagram access token cannot be null");
        }

        try {
            Influencer influencer = getInfluencerById(id);

            if (influencer.getStatus() != InfluencerStatus.PENDING_INSTAGRAM) {
                throw new InfluencerSetupException(
                        "Influencer must be in PENDING_INSTAGRAM state to connect instagram");
            }

            influencer.setInstagramAccessToken(token);
            influencer.setIsInstagramConnected(true);
            influencer.setStatus(InfluencerStatus.ACTIVE);

            influencerValidator.validateUpdate(influencer);

            return influencerRepository.save(influencer);
        } catch (InfluencerNotFoundException | InfluencerSetupException | InvalidInfluencerDataException e) {
            throw e;
        } catch (Exception e) {
            throw new InfluencerServiceException("Failed to connect Instagram for influencer: " + id, e);
        }
    }

    @Override
    public Influencer getInfluencerUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInfluencerDataException("Username cannot be null or empty");
        }

        try {
            InfluencerApplication application = applicationRepository.findByUsername(username)
                    .orElseThrow(
                            () -> new InfluencerNotFoundException("Influencer not found with username: " + username));

            return influencerRepository.findByApplicationId(application.getId())
                    .orElseThrow(
                            () -> new InfluencerNotFoundException("Influencer not found with username: " + username));
        } catch (InfluencerNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfluencerServiceException("Error retrieving influencer with username: " + username, e);
        }
    }
}
