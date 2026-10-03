package fontys.sem3.likeme.business.impl.user.influencer.application;

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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.user.influencer.ApplicationPhotosDeletionException;
import fontys.sem3.likeme.business.exception.user.influencer.DuplicateInfluencerDataException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerNotFoundException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerApplicationException;
import fontys.sem3.likeme.business.exception.utils.file.FileStorageException;
import fontys.sem3.likeme.business.interfaces.security.setuptoken.SetupTokenService;
import fontys.sem3.likeme.business.interfaces.user.influencer.InfluencerService;
import fontys.sem3.likeme.business.interfaces.utils.EmailService;
import fontys.sem3.likeme.business.interfaces.utils.FileStorageService;
import fontys.sem3.likeme.business.validator.user.influencer.InfluencerApplicationValidator;
import fontys.sem3.likeme.domain.security.setuptoken.SetupToken;
import fontys.sem3.likeme.domain.user.BillingDetails;
import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import fontys.sem3.likeme.repository.interfaces.influencer.InfluencerApplicationRepository;

@ExtendWith(MockitoExtension.class)
class InfluencerApplicationServiceImplTest {
        @Mock
        private InfluencerApplicationRepository applicationRepository;

        @Mock
        private InfluencerApplicationValidator applicationValidator;

        @Mock
        private EmailService emailService;

        @Mock
        private FileStorageService fileStorageService;

        @Mock
        private InfluencerService influencerService;

        @Mock
        private SetupTokenService setupTokenService;

        @InjectMocks
        private InfluencerApplicationServiceImpl applicationService;

        private InfluencerApplication testApplication;
        private BillingDetails testBillingDetails;
        private static final Long APPLICATION_ID = 1L;
        private static final String USERNAME = "testinfluencer";
        private static final String EMAIL = "influencer@test.com";
        private static final String PHONE = "+31612345678";
        private static final String INSTAGRAM = "@testinfluencer";
        private static final String PROFILE_PHOTO = "photos/profile.jpg";
        private static final String COVER_PHOTO = "photos/cover.jpg";
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
                                .phoneNumber(PHONE)
                                .about("Test influencer bio")
                                .instagramHandle(INSTAGRAM)
                                .isApproved(false)
                                .billingDetails(testBillingDetails)
                                .profilePhotoPath(PROFILE_PHOTO)
                                .coverPhotoPath(COVER_PHOTO)
                                .createdOn(CURRENT_TIME)
                                .updatedOn(CURRENT_TIME)
                                .build();
        }

        @Test
        void getAllApplications_Success() {
                List<InfluencerApplication> expectedApplications = Arrays.asList(testApplication);
                when(applicationRepository.findAll()).thenReturn(expectedApplications);

                List<InfluencerApplication> result = applicationService.getInfluencerApplications();

                assertEquals(expectedApplications, result);
                verify(applicationRepository).findAll();
                verifyNoMoreInteractions(applicationRepository);
        }

        @Test
        void getApplicationById_Success() {
                when(applicationRepository.findById(APPLICATION_ID))
                                .thenReturn(Optional.of(testApplication));

                InfluencerApplication result = applicationService.getInfluencerApplicationById(APPLICATION_ID);

                assertEquals(testApplication, result);
                verify(applicationRepository).findById(APPLICATION_ID);
                verifyNoMoreInteractions(applicationRepository);
        }

        @Test
        void getApplicationById_NotFound_ThrowsException() {
                when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.empty());

                InfluencerNotFoundException exception = assertThrows(
                                InfluencerNotFoundException.class,
                                () -> applicationService.getInfluencerApplicationById(APPLICATION_ID));

                assertEquals("Influencer application not found with id: " + APPLICATION_ID,
                                exception.getMessage());
                verify(applicationRepository).findById(APPLICATION_ID);
                verifyNoMoreInteractions(applicationRepository);
        }

        @Test
        void createApplication_Success() {
                when(applicationRepository.save(testApplication)).thenReturn(testApplication);

                InfluencerApplication result = applicationService.createInfluencerApplication(testApplication);

                assertEquals(testApplication, result);
                verify(applicationValidator).validateCreate(testApplication);
                verify(applicationRepository).save(testApplication);
                verifyNoMoreInteractions(applicationRepository, applicationValidator);
        }

        @Test
        void updateApplication_Success() {
                InfluencerApplication existingApplication = InfluencerApplication.builder()
                                .id(APPLICATION_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .phoneNumber(PHONE)
                                .instagramHandle(INSTAGRAM)
                                .profilePhotoPath(PROFILE_PHOTO)
                                .coverPhotoPath(COVER_PHOTO)
                                .isApproved(false)
                                .billingDetails(testBillingDetails)
                                .build();

                InfluencerApplication updatedApplication = InfluencerApplication.builder()
                                .id(APPLICATION_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .phoneNumber(PHONE)
                                .instagramHandle(INSTAGRAM)
                                .profilePhotoPath(PROFILE_PHOTO)
                                .coverPhotoPath(COVER_PHOTO)
                                .isApproved(true)
                                .billingDetails(testBillingDetails)
                                .build();

                SetupToken testToken = SetupToken.builder()
                                .token("test-token")
                                .build();

                when(applicationRepository.findById(APPLICATION_ID))
                                .thenReturn(Optional.of(existingApplication));
                when(influencerService.existsByApplicationId(APPLICATION_ID))
                                .thenReturn(false);
                when(applicationRepository.save(any(InfluencerApplication.class)))
                                .thenReturn(updatedApplication);
                when(setupTokenService.createToken(updatedApplication))
                                .thenReturn(testToken);

                InfluencerApplication result = applicationService.updateInfluencerApplication(updatedApplication);

                assertNotNull(result);
                assertTrue(result.getIsApproved());

                verify(applicationRepository).findById(APPLICATION_ID);
                verify(applicationValidator).validateUpdate(updatedApplication);
                verify(applicationRepository).save(updatedApplication);
                verify(setupTokenService).createToken(updatedApplication);
                verify(influencerService).createPendingInfluencer(updatedApplication);
                verify(emailService).sendApprovalEmail(EMAIL, USERNAME, testToken.getToken());
                verifyNoMoreInteractions(emailService, setupTokenService);
        }

        @Test
        void deleteApplication_Success() {
                when(applicationRepository.findById(APPLICATION_ID))
                                .thenReturn(Optional.of(testApplication));

                applicationService.deleteInfluencerApplication(APPLICATION_ID);

                verify(applicationRepository).findById(APPLICATION_ID);
                verify(applicationRepository).deleteById(APPLICATION_ID);
                verify(fileStorageService).deleteFile(PROFILE_PHOTO);
                verify(fileStorageService).deleteFile(COVER_PHOTO);
                verifyNoMoreInteractions(applicationRepository, fileStorageService);
        }

        @Test
        void deleteApplication_PhotoDeletionError_ThrowsException() {
                when(applicationRepository.findById(APPLICATION_ID))
                                .thenReturn(Optional.of(testApplication));
                doThrow(new FileStorageException("Storage error"))
                                .when(fileStorageService).deleteFile(PROFILE_PHOTO);

                ApplicationPhotosDeletionException exception = assertThrows(
                                ApplicationPhotosDeletionException.class,
                                () -> applicationService.deleteInfluencerApplication(APPLICATION_ID));

                assertTrue(exception.isApplicationDeleted());
                assertFalse(exception.isProfilePhotoDeleted());
                verify(applicationRepository).findById(APPLICATION_ID);
                verify(applicationRepository).deleteById(APPLICATION_ID);
                verify(fileStorageService).deleteFile(PROFILE_PHOTO);
        }

        @Test
        void getAllApplications_RepositoryError_ThrowsException() {
                when(applicationRepository.findAll())
                                .thenThrow(new RuntimeException("Database error"));

                InvalidInfluencerApplicationException exception = assertThrows(
                                InvalidInfluencerApplicationException.class,
                                () -> applicationService.getInfluencerApplications());

                assertEquals("Failed to retrieve influencer applications", exception.getMessage());
                verify(applicationRepository).findAll();
        }

        @Test
        void getApplicationById_NullId_ThrowsException() {
                InvalidInfluencerApplicationException exception = assertThrows(
                                InvalidInfluencerApplicationException.class,
                                () -> applicationService.getInfluencerApplicationById(null));

                assertEquals("Application ID cannot be null", exception.getMessage());
                verifyNoInteractions(applicationRepository);
        }

        @Test
        void getApplicationById_RepositoryError_ThrowsException() {
                when(applicationRepository.findById(APPLICATION_ID))
                                .thenThrow(new RuntimeException("Database error"));

                InvalidInfluencerApplicationException exception = assertThrows(
                                InvalidInfluencerApplicationException.class,
                                () -> applicationService.getInfluencerApplicationById(APPLICATION_ID));

                assertEquals("Error retrieving application with id: " + APPLICATION_ID,
                                exception.getMessage());
                verify(applicationRepository).findById(APPLICATION_ID);
        }

        @Test
        void createApplication_NullApplication_ThrowsException() {
                InvalidInfluencerApplicationException exception = assertThrows(
                                InvalidInfluencerApplicationException.class,
                                () -> applicationService.createInfluencerApplication(null));

                assertEquals("Application cannot be null", exception.getMessage());
                verifyNoInteractions(applicationRepository, applicationValidator);
        }

        @Test
        void createApplication_DuplicateData_ThrowsException() {
                doThrow(new DuplicateInfluencerDataException("Username already exists", "username"))
                                .when(applicationValidator).validateCreate(testApplication);

                DuplicateInfluencerDataException exception = assertThrows(
                                DuplicateInfluencerDataException.class,
                                () -> applicationService.createInfluencerApplication(testApplication));

                assertEquals("username", exception.getMessage());
                verify(applicationValidator).validateCreate(testApplication);
                verifyNoInteractions(applicationRepository);
        }

        @Test
        void createApplication_RepositoryError_ThrowsException() {
                when(applicationRepository.save(testApplication))
                                .thenThrow(new RuntimeException("Database error"));

                InvalidInfluencerApplicationException exception = assertThrows(
                                InvalidInfluencerApplicationException.class,
                                () -> applicationService.createInfluencerApplication(testApplication));

                assertEquals("Failed to create influencer application", exception.getMessage());
                verify(applicationValidator).validateCreate(testApplication);
                verify(applicationRepository).save(testApplication);
        }

        @Test
        void updateApplication_NullApplication_ThrowsException() {
                InvalidInfluencerApplicationException exception = assertThrows(
                                InvalidInfluencerApplicationException.class,
                                () -> applicationService.updateInfluencerApplication(null));

                assertEquals("Application and ID cannot be null", exception.getMessage());
                verifyNoInteractions(applicationRepository, applicationValidator);
        }

        @Test
        void updateApplication_ExistingInfluencer_ThrowsException() {
                when(applicationRepository.findById(APPLICATION_ID))
                                .thenReturn(Optional.of(testApplication));
                when(influencerService.existsByApplicationId(APPLICATION_ID))
                                .thenReturn(true);

                InvalidInfluencerApplicationException exception = assertThrows(
                                InvalidInfluencerApplicationException.class,
                                () -> applicationService.updateInfluencerApplication(testApplication));

                assertEquals("Influencer already exists for this application", exception.getMessage());
                verify(applicationRepository).findById(APPLICATION_ID);
                verify(influencerService).existsByApplicationId(APPLICATION_ID);
                verifyNoMoreInteractions(applicationRepository);
        }

        @Test
        void updateApplication_ProcessApprovalError_ThrowsException() {
                InfluencerApplication existingApplication = InfluencerApplication.builder()
                                .id(APPLICATION_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .phoneNumber(PHONE)
                                .instagramHandle(INSTAGRAM)
                                .profilePhotoPath(PROFILE_PHOTO)
                                .coverPhotoPath(COVER_PHOTO)
                                .isApproved(false)
                                .billingDetails(testBillingDetails)
                                .build();

                InfluencerApplication updatedApplication = InfluencerApplication.builder()
                                .id(APPLICATION_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .phoneNumber(PHONE)
                                .instagramHandle(INSTAGRAM)
                                .profilePhotoPath(PROFILE_PHOTO)
                                .coverPhotoPath(COVER_PHOTO)
                                .isApproved(true)
                                .billingDetails(testBillingDetails)
                                .build();

                when(applicationRepository.findById(APPLICATION_ID))
                                .thenReturn(Optional.of(existingApplication));
                when(influencerService.existsByApplicationId(APPLICATION_ID))
                                .thenReturn(false);
                when(applicationRepository.save(updatedApplication))
                                .thenReturn(updatedApplication);
                when(setupTokenService.createToken(updatedApplication))
                                .thenThrow(new RuntimeException("Token creation failed"));

                InvalidInfluencerApplicationException exception = assertThrows(
                                InvalidInfluencerApplicationException.class,
                                () -> applicationService.updateInfluencerApplication(updatedApplication));

                assertEquals("Failed to process approval workflow", exception.getMessage());
                verify(applicationRepository).findById(APPLICATION_ID);
                verify(applicationValidator).validateUpdate(updatedApplication);
                verify(applicationRepository).save(updatedApplication);
                verify(setupTokenService).createToken(updatedApplication);
        }

        @Test
        void deleteApplication_NullId_ThrowsException() {
                InvalidInfluencerApplicationException exception = assertThrows(
                                InvalidInfluencerApplicationException.class,
                                () -> applicationService.deleteInfluencerApplication(null));

                assertEquals("Application ID cannot be null", exception.getMessage());
                verifyNoInteractions(applicationRepository, fileStorageService);
        }

        @Test
        void deleteApplication_RepositoryError_ThrowsException() {
                when(applicationRepository.findById(APPLICATION_ID))
                                .thenReturn(Optional.of(testApplication));
                doThrow(new RuntimeException("Database error"))
                                .when(applicationRepository).deleteById(APPLICATION_ID);

                InvalidInfluencerApplicationException exception = assertThrows(
                                InvalidInfluencerApplicationException.class,
                                () -> applicationService.deleteInfluencerApplication(APPLICATION_ID));

                assertEquals("Failed to delete influencer application", exception.getMessage());
                verify(applicationRepository).findById(APPLICATION_ID);
                verify(applicationRepository).deleteById(APPLICATION_ID);
                verifyNoInteractions(fileStorageService);
        }

        @Test
        void deleteApplication_CoverPhotoDeletionError_ThrowsException() {
                when(applicationRepository.findById(APPLICATION_ID))
                                .thenReturn(Optional.of(testApplication));

                // Successfully delete profile photo
                doNothing().when(fileStorageService).deleteFile(PROFILE_PHOTO);

                // Throw exception when deleting cover photo
                doThrow(new FileStorageException("Storage error"))
                                .when(fileStorageService).deleteFile(COVER_PHOTO);

                ApplicationPhotosDeletionException exception = assertThrows(
                                ApplicationPhotosDeletionException.class,
                                () -> applicationService.deleteInfluencerApplication(APPLICATION_ID));

                assertTrue(exception.isApplicationDeleted());
                assertTrue(exception.isProfilePhotoDeleted());
                assertFalse(exception.isCoverPhotoDeleted());
                assertEquals("Application deleted but some photos could not be deleted",
                                exception.getMessage());

                verify(applicationRepository).findById(APPLICATION_ID);
                verify(applicationRepository).deleteById(APPLICATION_ID);
                verify(fileStorageService).deleteFile(PROFILE_PHOTO);
                verify(fileStorageService).deleteFile(COVER_PHOTO);
        }
}