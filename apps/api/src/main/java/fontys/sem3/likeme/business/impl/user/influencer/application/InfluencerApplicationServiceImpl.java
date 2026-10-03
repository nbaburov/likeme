package fontys.sem3.likeme.business.impl.user.influencer.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fontys.sem3.likeme.business.exception.utils.file.FileNotFoundException;
import fontys.sem3.likeme.business.exception.utils.file.FileStorageException;
import fontys.sem3.likeme.business.exception.user.influencer.ApplicationPhotosDeletionException;
import fontys.sem3.likeme.business.exception.user.influencer.DuplicateInfluencerDataException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerNotFoundException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerApplicationException;
import fontys.sem3.likeme.business.interfaces.user.influencer.InfluencerApplicationService;
import fontys.sem3.likeme.business.interfaces.user.influencer.InfluencerService;
import fontys.sem3.likeme.business.interfaces.security.setuptoken.SetupTokenService;
import fontys.sem3.likeme.business.interfaces.utils.EmailService;
import fontys.sem3.likeme.business.interfaces.utils.FileStorageService;
import fontys.sem3.likeme.business.validator.user.influencer.InfluencerApplicationValidator;
import fontys.sem3.likeme.domain.security.setuptoken.SetupToken;
import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import fontys.sem3.likeme.repository.interfaces.influencer.InfluencerApplicationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InfluencerApplicationServiceImpl implements InfluencerApplicationService {
    private final InfluencerApplicationRepository influencerApplicationRepository;
    private final InfluencerApplicationValidator influencerApplicationValidator;
    private final EmailService emailService;
    private final FileStorageService fileStorageService;
    private final InfluencerService influencerService;
    private final SetupTokenService setupTokenService;

    @Override
    public List<InfluencerApplication> getInfluencerApplications() {
        try {
            return influencerApplicationRepository.findAll();
        } catch (Exception e) {
            throw new InvalidInfluencerApplicationException("Failed to retrieve influencer applications", e);
        }
    }

    @Override
    public InfluencerApplication getInfluencerApplicationById(Long id) {
        if (id == null) {
            throw new InvalidInfluencerApplicationException("Application ID cannot be null");
        }
        try {
            return influencerApplicationRepository.findById(id)
                    .orElseThrow(
                            () -> new InfluencerNotFoundException("Influencer application not found with id: " + id));
        } catch (InfluencerNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidInfluencerApplicationException("Error retrieving application with id: " + id, e);
        }
    }

    @Transactional
    @Override
    public InfluencerApplication createInfluencerApplication(InfluencerApplication application) {
        if (application == null) {
            throw new InvalidInfluencerApplicationException("Application cannot be null");
        }

        try {
            influencerApplicationValidator.validateCreate(application);
            return influencerApplicationRepository.save(application);
        } catch (DuplicateInfluencerDataException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidInfluencerApplicationException("Failed to create influencer application", e);
        }
    }

    @Transactional
    @Override
    public InfluencerApplication updateInfluencerApplication(InfluencerApplication application) {
        if (application == null || application.getId() == null) {
            throw new InvalidInfluencerApplicationException("Application and ID cannot be null");
        }

        try {
            InfluencerApplication existingApplication = getInfluencerApplicationById(application.getId());

            if (influencerService.existsByApplicationId(application.getId())) {
                throw new InvalidInfluencerApplicationException("Influencer already exists for this application");
            }

            influencerApplicationValidator.validateUpdate(application);
            InfluencerApplication updatedApplication = influencerApplicationRepository.save(application);

            if (Boolean.TRUE.equals(updatedApplication.getIsApproved())
                    && !Boolean.TRUE.equals(existingApplication.getIsApproved())
                    && !influencerService.existsByApplicationId(application.getId())) {
                processApproval(updatedApplication);
            }

            return updatedApplication;
        } catch (InfluencerNotFoundException | DuplicateInfluencerDataException
                | InvalidInfluencerApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidInfluencerApplicationException("Failed to update influencer application", e);
        }
    }

    private void processApproval(InfluencerApplication application) {
        try {
            SetupToken token = setupTokenService.createToken(application);
            influencerService.createPendingInfluencer(application);
            emailService.sendApprovalEmail(application.getEmail(), application.getUsername(), token.getToken());
        } catch (Exception e) {
            throw new InvalidInfluencerApplicationException("Failed to process approval workflow", e);
        }
    }

    @Override
    public void deleteInfluencerApplication(Long id) {
        if (id == null) {
            throw new InvalidInfluencerApplicationException("Application ID cannot be null");
        }

        try {
            InfluencerApplication application = getInfluencerApplicationById(id);
            deleteApplicationEntity(id);
            deleteApplicationPhotos(application);
        } catch (InfluencerNotFoundException | ApplicationPhotosDeletionException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidInfluencerApplicationException("Failed to delete influencer application", e);
        }
    }

    private void deleteApplicationEntity(Long id) {
        influencerApplicationRepository.deleteById(id);
    }

    private void deleteApplicationPhotos(InfluencerApplication application) {
        boolean profilePhotoDeleted = true;
        boolean coverPhotoDeleted = true;

        if (application.getProfilePhotoPath() != null) {
            try {
                fileStorageService.deleteFile(application.getProfilePhotoPath());
            } catch (FileNotFoundException | FileStorageException e) {
                profilePhotoDeleted = false;
            }
        }

        if (application.getCoverPhotoPath() != null) {
            try {
                fileStorageService.deleteFile(application.getCoverPhotoPath());
            } catch (FileNotFoundException | FileStorageException e) {
                coverPhotoDeleted = false;
            }
        }

        if (!profilePhotoDeleted || !coverPhotoDeleted) {
            throw new ApplicationPhotosDeletionException(
                    "Application deleted but some photos could not be deleted",
                    true,
                    profilePhotoDeleted,
                    coverPhotoDeleted);
        }
    }
}
