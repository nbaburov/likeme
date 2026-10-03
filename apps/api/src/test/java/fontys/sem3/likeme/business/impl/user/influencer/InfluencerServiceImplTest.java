package fontys.sem3.likeme.business.impl.user.influencer;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.security.setuptoken.InvalidSetupTokenException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerNotFoundException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerServiceException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerSetupException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerDataException;
import fontys.sem3.likeme.business.interfaces.security.PasswordService;
import fontys.sem3.likeme.business.interfaces.security.setuptoken.SetupTokenService;
import fontys.sem3.likeme.business.validator.user.influencer.InfluencerValidator;
import fontys.sem3.likeme.domain.security.setuptoken.SetupToken;
import fontys.sem3.likeme.domain.user.BillingDetails;
import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import fontys.sem3.likeme.domain.user.influencer.InfluencerStatus;
import fontys.sem3.likeme.repository.interfaces.influencer.InfluencerApplicationRepository;
import fontys.sem3.likeme.repository.interfaces.influencer.InfluencerRepository;
import fontys.sem3.likeme.business.validator.user.influencer.InfluencerApplicationValidator;

@ExtendWith(MockitoExtension.class)
class InfluencerServiceImplTest {
        @Mock
        private InfluencerRepository influencerRepository;

        @Mock
        private InfluencerApplicationRepository applicationRepository;

        @Mock
        private SetupTokenService setupTokenService;

        @Mock
        private InfluencerValidator influencerValidator;

        @Mock
        private PasswordService passwordService;

        @Mock
        private InfluencerApplicationValidator influencerApplicationValidator;

        @InjectMocks
        private InfluencerServiceImpl influencerService;

        private Influencer testInfluencer;
        private InfluencerApplication testApplication;
        private BillingDetails testBillingDetails;
        private static final Long INFLUENCER_ID = 1L;
        private static final Long APPLICATION_ID = 1L;
        private static final String USERNAME = "testinfluencer";
        private static final String EMAIL = "influencer@test.com";
        private static final String PASSWORD = "password123";
        private static final String SALT = "testsalt";
        private static final String HASHED_PASSWORD = "hashedpassword123";
        private static final String INSTAGRAM_TOKEN = "instagram_token_123";
        private static final Date CURRENT_TIME = new Date();

        @BeforeEach
        void setUp() {
                testBillingDetails = BillingDetails.builder()
                                .firstName("John")
                                .lastName("Doe")
                                .country("Netherlands")
                                .streetAddress("Test Street 123")
                                .city("Eindhoven")
                                .state("NB")
                                .zipCode("5611")
                                .build();

                testApplication = InfluencerApplication.builder()
                                .id(APPLICATION_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .billingDetails(testBillingDetails)
                                .isApproved(true)
                                .build();

                testInfluencer = Influencer.builder()
                                .id(INFLUENCER_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .password(HASHED_PASSWORD)
                                .salt(SALT)
                                .role(Role.INFLUENCER)
                                .application(testApplication)
                                .isInstagramConnected(false)
                                .status(InfluencerStatus.PENDING_SETUP)
                                .createdOn(CURRENT_TIME)
                                .updatedOn(CURRENT_TIME)
                                .isActive(true)
                                .build();
        }

        @Test
        void createPendingInfluencer_Success() {
                InfluencerApplication savedApplication = testApplication;
                Influencer expectedInfluencer = Influencer.builder()
                                .application(savedApplication)
                                .status(InfluencerStatus.PENDING_SETUP)
                                .isInstagramConnected(false)
                                .build();

                when(applicationRepository.save(testApplication)).thenReturn(savedApplication);
                when(influencerRepository.save(expectedInfluencer)).thenReturn(expectedInfluencer);

                Influencer result = influencerService.createPendingInfluencer(testApplication);

                assertNotNull(result);
                assertEquals(InfluencerStatus.PENDING_SETUP, result.getStatus());
                assertFalse(result.getIsInstagramConnected());
                assertEquals(savedApplication, result.getApplication());

                verify(influencerValidator).validateCreate(expectedInfluencer);
                verify(applicationRepository).save(testApplication);
                verify(influencerRepository).save(expectedInfluencer);
        }

        @Test
        void completeInfluencerSetup_Success() {
                // Arrange
                String setupToken = "valid_token";
                String password = "testPassword123";
                String salt = "generatedSalt123";
                String hashedPassword = "hashedPassword123";

                SetupToken token = SetupToken.builder()
                                .token(setupToken)
                                .application(testApplication)
                                .build();

                testInfluencer.setStatus(InfluencerStatus.PENDING_SETUP);

                when(setupTokenService.validateToken(setupToken)).thenReturn(token);
                when(influencerRepository.findByApplicationId(testApplication.getId()))
                                .thenReturn(Optional.of(testInfluencer));
                when(passwordService.generateSalt()).thenReturn(salt);
                when(passwordService.hashPassword(password, salt)).thenReturn(hashedPassword);
                when(influencerRepository.save(any(Influencer.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                Influencer result = influencerService.completeInfluencerSetup(setupToken, password);

                // Assert
                assertEquals(InfluencerStatus.PENDING_INSTAGRAM, result.getStatus());
                assertEquals(hashedPassword, result.getPassword());
                assertEquals(salt, result.getSalt());

                // Verify
                verify(setupTokenService).validateToken(setupToken);
                verify(influencerRepository).findByApplicationId(testApplication.getId());
                verify(passwordService).generateSalt();
                verify(passwordService).hashPassword(password, salt);
                verify(influencerValidator).validateUpdate(any(Influencer.class));
                verify(setupTokenService).invalidateToken(setupToken);
                verify(influencerRepository).save(any(Influencer.class));
        }

        @Test
        void completeInfluencerSetup_InvalidToken_ThrowsException() {
                String invalidToken = "invalid_token";
                when(setupTokenService.validateToken(invalidToken))
                                .thenThrow(new InvalidSetupTokenException("Invalid token"));

                InvalidSetupTokenException exception = assertThrows(
                                InvalidSetupTokenException.class,
                                () -> influencerService.completeInfluencerSetup(invalidToken, PASSWORD));

                assertEquals("Invalid token", exception.getMessage());
                verify(setupTokenService).validateToken(invalidToken);
                verifyNoInteractions(influencerRepository, passwordService);
        }

        @Test
        void getAllInfluencers_Success() {
                List<Influencer> expectedInfluencers = Arrays.asList(testInfluencer);
                when(influencerRepository.findAll()).thenReturn(expectedInfluencers);

                List<Influencer> result = influencerService.getAllInfluencers();

                assertEquals(expectedInfluencers, result);
                verify(influencerRepository).findAll();
        }

        @Test
        void getInfluencerById_Success() {
                when(influencerRepository.findById(INFLUENCER_ID))
                                .thenReturn(Optional.of(testInfluencer));

                Influencer result = influencerService.getInfluencerById(INFLUENCER_ID);

                assertEquals(testInfluencer, result);
                verify(influencerRepository).findById(INFLUENCER_ID);
        }

        @Test
        void getInfluencerById_NotFound_ThrowsException() {
                when(influencerRepository.findById(INFLUENCER_ID))
                                .thenReturn(Optional.empty());

                InfluencerNotFoundException exception = assertThrows(
                                InfluencerNotFoundException.class,
                                () -> influencerService.getInfluencerById(INFLUENCER_ID));

                assertEquals("Influencer not found with id: " + INFLUENCER_ID, exception.getMessage());
                verify(influencerRepository).findById(INFLUENCER_ID);
        }

        @Test
        void connectInstagram_NullToken_ThrowsException() {
                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.connectInstagram(INFLUENCER_ID, null));

                assertEquals("Instagram access token cannot be null", exception.getMessage());
                verifyNoInteractions(influencerRepository);
        }

        @Test
        void connectInstagram_EmptyToken_ThrowsException() {
                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.connectInstagram(INFLUENCER_ID, ""));

                assertEquals("Instagram access token cannot be null", exception.getMessage());
                verifyNoInteractions(influencerRepository);
        }

        @Test
        void connectInstagram_NullId_ThrowsException() {
                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.connectInstagram(null, "valid_token"));

                assertEquals("Influencer ID cannot be null", exception.getMessage());
                verifyNoInteractions(influencerRepository);
        }

        @Test
        void connectInstagram_Success() {
                testInfluencer.setStatus(InfluencerStatus.PENDING_INSTAGRAM);
                when(influencerRepository.findById(INFLUENCER_ID))
                                .thenReturn(Optional.of(testInfluencer));
                when(influencerRepository.save(any(Influencer.class)))
                                .thenReturn(testInfluencer);

                Influencer result = influencerService.connectInstagram(INFLUENCER_ID, "valid_token");

                assertNotNull(result);
                assertEquals(InfluencerStatus.ACTIVE, result.getStatus());
                assertTrue(result.getIsInstagramConnected());
                assertEquals("valid_token", result.getInstagramAccessToken());

                verify(influencerRepository).findById(INFLUENCER_ID);
                verify(influencerValidator).validateUpdate(any(Influencer.class));
                verify(influencerRepository).save(any(Influencer.class));
        }

        @Test
        void connectInstagram_WrongStatus_ThrowsException() {
                testInfluencer.setStatus(InfluencerStatus.ACTIVE);
                when(influencerRepository.findById(INFLUENCER_ID))
                                .thenReturn(Optional.of(testInfluencer));

                InfluencerSetupException exception = assertThrows(
                                InfluencerSetupException.class,
                                () -> influencerService.connectInstagram(INFLUENCER_ID, "valid_token"));

                assertEquals("Influencer must be in PENDING_INSTAGRAM state to connect instagram",
                                exception.getMessage());
                verify(influencerRepository).findById(INFLUENCER_ID);
                verifyNoMoreInteractions(influencerRepository);
        }

        @Test
        void createPendingInfluencer_NullApplication_ThrowsException() {
                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.createPendingInfluencer(null));

                assertEquals("Influencer application cannot be null", exception.getMessage());
                verifyNoInteractions(applicationRepository, influencerRepository);
        }

        @Test
        void createPendingInfluencer_ValidationError_ThrowsException() {
                when(applicationRepository.save(testApplication))
                                .thenReturn(testApplication);

                Influencer expectedInfluencer = Influencer.builder()
                                .application(testApplication)
                                .status(InfluencerStatus.PENDING_SETUP)
                                .isInstagramConnected(false)
                                .build();

                doThrow(new InvalidInfluencerDataException("Validation failed"))
                                .when(influencerValidator)
                                .validateCreate(argThat(
                                                influencer -> influencer.getApplication().equals(testApplication) &&
                                                                influencer.getStatus() == InfluencerStatus.PENDING_SETUP
                                                                &&
                                                                !influencer.getIsInstagramConnected()));

                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.createPendingInfluencer(testApplication));

                assertEquals("Validation failed", exception.getMessage());
                verify(applicationRepository).save(testApplication);
                verify(influencerValidator).validateCreate(
                                argThat(influencer -> influencer.getApplication().equals(testApplication) &&
                                                influencer.getStatus() == InfluencerStatus.PENDING_SETUP &&
                                                !influencer.getIsInstagramConnected()));
                verifyNoMoreInteractions(influencerRepository);
        }

        @Test
        void completeInfluencerSetup_NullPassword_ThrowsException() {
                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.completeInfluencerSetup("token", null));

                assertEquals("Password cannot be empty", exception.getMessage());
                verifyNoInteractions(setupTokenService, influencerRepository);
        }

        @Test
        void completeInfluencerSetup_EmptyPassword_ThrowsException() {
                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.completeInfluencerSetup("token", ""));

                assertEquals("Password cannot be empty", exception.getMessage());
                verifyNoInteractions(setupTokenService, influencerRepository);
        }

        @Test
        void completeInfluencerSetup_WrongStatus_ThrowsException() {
                String setupToken = "valid_token";
                testInfluencer.setStatus(InfluencerStatus.ACTIVE);

                SetupToken token = SetupToken.builder()
                                .token(setupToken)
                                .application(testApplication)
                                .build();

                when(setupTokenService.validateToken(setupToken)).thenReturn(token);
                when(influencerRepository.findByApplicationId(testApplication.getId()))
                                .thenReturn(Optional.of(testInfluencer));

                InfluencerSetupException exception = assertThrows(
                                InfluencerSetupException.class,
                                () -> influencerService.completeInfluencerSetup(setupToken, PASSWORD));

                assertEquals("Influencer must be in PENDING_SETUP state to complete setup",
                                exception.getMessage());
                verify(setupTokenService).validateToken(setupToken);
                verify(influencerRepository).findByApplicationId(testApplication.getId());
        }

        @Test
        void getAllInfluencers_RepositoryError_ThrowsException() {
                when(influencerRepository.findAll())
                                .thenThrow(new RuntimeException("Database error"));

                InfluencerServiceException exception = assertThrows(
                                InfluencerServiceException.class,
                                () -> influencerService.getAllInfluencers());

                assertEquals("Failed to retrieve all influencers", exception.getMessage());
                verify(influencerRepository).findAll();
        }

        @Test
        void getInfluencerById_NullId_ThrowsException() {
                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.getInfluencerById(null));

                assertEquals("Influencer ID cannot be null", exception.getMessage());
                verifyNoInteractions(influencerRepository);
        }

        @Test
        void getInfluencerByApplicationId_NullId_ThrowsException() {
                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.getInfluencerByApplicationId(null));

                assertEquals("Application ID cannot be null", exception.getMessage());
                verifyNoInteractions(influencerRepository);
        }

        @Test
        void getInfluencerByApplicationId_NotFound_ThrowsException() {
                when(influencerRepository.findByApplicationId(APPLICATION_ID))
                                .thenReturn(Optional.empty());

                InfluencerNotFoundException exception = assertThrows(
                                InfluencerNotFoundException.class,
                                () -> influencerService.getInfluencerByApplicationId(APPLICATION_ID));

                assertEquals("Influencer not found for application: " + APPLICATION_ID,
                                exception.getMessage());
                verify(influencerRepository).findByApplicationId(APPLICATION_ID);
        }

        @Test
        void createInfluencer_Success() {
                // Arrange
                String rawPassword = "password123";
                String salt = "generatedSalt";
                String hashedPassword = "hashedPassword";

                Influencer influencerToCreate = Influencer.builder()
                                .application(testApplication)
                                .password(rawPassword)
                                .build();

                // Mock all necessary dependencies
                when(passwordService.generateSalt()).thenReturn(salt);
                when(passwordService.hashPassword(rawPassword, salt)).thenReturn(hashedPassword);
                when(applicationRepository.save(testApplication)).thenReturn(testApplication);
                when(influencerRepository.save(argThat(influencer -> influencer.getPassword().equals(hashedPassword) &&
                                influencer.getSalt().equals(salt) &&
                                influencer.getApplication().equals(testApplication)))).thenReturn(testInfluencer);

                // Act
                Influencer result = influencerService.createInfluencer(influencerToCreate);

                // Assert
                assertNotNull(result);
                assertEquals(testInfluencer, result);

                // Verify all interactions
                verify(passwordService).generateSalt();
                verify(passwordService).hashPassword(rawPassword, salt);
                verify(influencerApplicationValidator).validateCreate(testApplication);
                verify(applicationRepository).save(testApplication);
                verify(influencerValidator)
                                .validateCreate(argThat(influencer -> influencer.getPassword().equals(hashedPassword) &&
                                                influencer.getSalt().equals(salt) &&
                                                influencer.getApplication().equals(testApplication)));
                verify(influencerRepository)
                                .save(argThat(influencer -> influencer.getPassword().equals(hashedPassword) &&
                                                influencer.getSalt().equals(salt) &&
                                                influencer.getApplication().equals(testApplication)));
        }

        @Test
        void createInfluencer_NullInfluencer_ThrowsException() {
                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.createInfluencer(null));

                assertEquals("Influencer cannot be null", exception.getMessage());
                verifyNoInteractions(passwordService, applicationRepository, influencerRepository);
        }

        @Test
        void updateInfluencer_Success_WithPasswordChange() {
                // Arrange
                String newPassword = "newPassword";
                String newSalt = "newSalt";
                String newHashedPassword = "newHashedPassword";

                Influencer influencerToUpdate = Influencer.builder()
                                .id(INFLUENCER_ID)
                                .application(testApplication)
                                .password(newPassword) // Different from existing password
                                .build();

                // Mock existing influencer
                when(influencerRepository.findById(INFLUENCER_ID))
                                .thenReturn(Optional.of(testInfluencer));

                // Mock password operations
                when(passwordService.generateSalt()).thenReturn(newSalt);
                when(passwordService.hashPassword(newPassword, newSalt))
                                .thenReturn(newHashedPassword);

                // Mock save operations
                when(applicationRepository.save(any(InfluencerApplication.class)))
                                .thenReturn(testApplication);
                when(influencerRepository
                                .save(argThat(influencer -> influencer.getPassword().equals(newHashedPassword) &&
                                                influencer.getSalt().equals(newSalt))))
                                .thenReturn(testInfluencer);

                // Act
                Influencer result = influencerService.updateInfluencer(influencerToUpdate);

                // Assert
                assertNotNull(result);

                // Verify all interactions in order
                verify(influencerRepository).findById(INFLUENCER_ID);
                verify(passwordService).generateSalt();
                verify(passwordService).hashPassword(newPassword, newSalt);
                verify(influencerValidator).validateUpdate(any(Influencer.class));
                verify(applicationRepository).save(any(InfluencerApplication.class));
                verify(influencerRepository)
                                .save(argThat(influencer -> influencer.getPassword().equals(newHashedPassword) &&
                                                influencer.getSalt().equals(newSalt)));
        }

        @Test
        void updateInfluencer_Success_WithoutPasswordChange() {
                when(influencerRepository.findById(INFLUENCER_ID)).thenReturn(Optional.of(testInfluencer));
                when(applicationRepository.save(any(InfluencerApplication.class))).thenReturn(testApplication);
                when(influencerRepository.save(any(Influencer.class))).thenReturn(testInfluencer);

                Influencer result = influencerService.updateInfluencer(testInfluencer);

                assertNotNull(result);
                verifyNoInteractions(passwordService);
                verify(influencerValidator).validateUpdate(any(Influencer.class));
                verify(applicationRepository).save(any(InfluencerApplication.class));
                verify(influencerRepository).save(any(Influencer.class));
        }

        @Test
        void deleteInfluencer_Success() {
                when(influencerRepository.existsById(INFLUENCER_ID)).thenReturn(true);

                influencerService.deleteInfluencer(INFLUENCER_ID);

                verify(influencerRepository).existsById(INFLUENCER_ID);
                verify(influencerRepository).deleteById(INFLUENCER_ID);
        }

        @Test
        void deleteInfluencer_NotFound_ThrowsException() {
                when(influencerRepository.existsById(INFLUENCER_ID)).thenReturn(false);

                InfluencerNotFoundException exception = assertThrows(
                                InfluencerNotFoundException.class,
                                () -> influencerService.deleteInfluencer(INFLUENCER_ID));

                assertEquals("Influencer not found with id: " + INFLUENCER_ID, exception.getMessage());
                verify(influencerRepository).existsById(INFLUENCER_ID);
        }

        @Test
        void existsByApplicationId_Success() {
                when(influencerRepository.existsByApplicationId(APPLICATION_ID)).thenReturn(true);

                boolean result = influencerService.existsByApplicationId(APPLICATION_ID);

                assertTrue(result);
                verify(influencerRepository).existsByApplicationId(APPLICATION_ID);
        }

        @Test
        void existsByApplicationId_NullId_ThrowsException() {
                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.existsByApplicationId(null));

                assertEquals("Application ID cannot be null", exception.getMessage());
                verifyNoInteractions(influencerRepository);
        }

        @Test
        void getInfluencerUsername_Success() {
                when(applicationRepository.findByUsername(USERNAME))
                                .thenReturn(Optional.of(testApplication));
                when(influencerRepository.findByApplicationId(APPLICATION_ID))
                                .thenReturn(Optional.of(testInfluencer));

                Influencer result = influencerService.getInfluencerUsername(USERNAME);

                assertNotNull(result);
                assertEquals(testInfluencer, result);
                verify(applicationRepository).findByUsername(USERNAME);
                verify(influencerRepository).findByApplicationId(APPLICATION_ID);
        }

        @Test
        void getInfluencerUsername_NullUsername_ThrowsException() {
                InvalidInfluencerDataException exception = assertThrows(
                                InvalidInfluencerDataException.class,
                                () -> influencerService.getInfluencerUsername(null));

                assertEquals("Username cannot be null or empty", exception.getMessage());
                verifyNoInteractions(applicationRepository, influencerRepository);
        }

        @Test
        void getInfluencerUsername_NotFound_ThrowsException() {
                when(applicationRepository.findByUsername(USERNAME))
                                .thenReturn(Optional.empty());

                InfluencerNotFoundException exception = assertThrows(
                                InfluencerNotFoundException.class,
                                () -> influencerService.getInfluencerUsername(USERNAME));

                assertEquals("Influencer not found with username: " + USERNAME, exception.getMessage());
                verify(applicationRepository).findByUsername(USERNAME);
                verifyNoInteractions(influencerRepository);
        }
}