package fontys.sem3.likeme.business.impl.user.client;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.user.client.ClientNotFoundException;
import fontys.sem3.likeme.business.exception.user.client.ClientServiceException;
import fontys.sem3.likeme.business.exception.user.client.DuplicateClientException;
import fontys.sem3.likeme.business.exception.user.client.InvalidClientDataException;
import fontys.sem3.likeme.business.exception.utils.file.FileNotFoundException;
import fontys.sem3.likeme.business.exception.utils.file.FileStorageException;
import fontys.sem3.likeme.business.interfaces.security.PasswordService;
import fontys.sem3.likeme.business.interfaces.utils.FileStorageService;
import fontys.sem3.likeme.business.validator.user.client.ClientValidator;
import fontys.sem3.likeme.domain.user.BillingDetails;
import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.repository.interfaces.client.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {
        @Mock
        private ClientRepository clientRepository;

        @Mock
        private ClientValidator clientValidator;

        @Mock
        private PasswordService passwordService;

        @Mock
        private FileStorageService fileStorageService;

        @InjectMocks
        private ClientServiceImpl clientService;

        private Client testClient;
        private BillingDetails testBillingDetails;
        private static final Long CLIENT_ID = 1L;
        private static final String USERNAME = "testuser";
        private static final String EMAIL = "test@example.com";
        private static final String PASSWORD = "password123";
        private static final String SALT = "testsalt";
        private static final String HASHED_PASSWORD = "hashedpassword123";
        private static final String PHOTO_PATH = "photos/profile.jpg";
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

                testClient = Client.builder()
                                .id(CLIENT_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .password(PASSWORD)
                                .salt(SALT)
                                .role(Role.CLIENT)
                                .profilePhotoPath(PHOTO_PATH)
                                .createdOn(CURRENT_TIME)
                                .updatedOn(CURRENT_TIME)
                                .isActive(true)
                                .lastLoginOn(CURRENT_TIME)
                                .instagramHandle("@testuser")
                                .isInstagramConnected(true)
                                .instagramAccessToken("instagram-token")
                                .billingDetails(testBillingDetails)
                                .build();
        }

        @Test
        void getAllClients_Success() {
                List<Client> expectedClients = Arrays.asList(testClient);
                when(clientRepository.findAll()).thenReturn(expectedClients);

                List<Client> result = clientService.getAllClients();

                assertEquals(expectedClients, result);
                verify(clientRepository).findAll();
                verifyNoMoreInteractions(clientRepository);
        }

        @Test
        void getClient_ValidId_Success() {
                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(testClient));

                Client result = clientService.getClient(CLIENT_ID);

                assertEquals(testClient, result);
                verify(clientRepository).findById(CLIENT_ID);
                verifyNoMoreInteractions(clientRepository);
        }

        @Test
        void getClient_NonexistentId_ThrowsException() {
                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());

                ClientNotFoundException exception = assertThrows(
                                ClientNotFoundException.class,
                                () -> clientService.getClient(CLIENT_ID));

                assertEquals("Client not found with id: " + CLIENT_ID, exception.getMessage());
                verify(clientRepository).findById(CLIENT_ID);
                verifyNoMoreInteractions(clientRepository);
        }

        @Test
        void createClient_Success() {
                Client newClient = Client.builder()
                                .username(USERNAME)
                                .email(EMAIL)
                                .password(PASSWORD)
                                .role(Role.CLIENT)
                                .billingDetails(testBillingDetails)
                                .build();

                when(passwordService.generateSalt()).thenReturn(SALT);
                when(passwordService.hashPassword(PASSWORD, SALT)).thenReturn(HASHED_PASSWORD);
                when(clientRepository.save(argThat(client -> client.getUsername().equals(USERNAME) &&
                                client.getEmail().equals(EMAIL) &&
                                client.getPassword().equals(HASHED_PASSWORD) &&
                                client.getSalt().equals(SALT)))).thenReturn(testClient);

                Client result = clientService.createClient(newClient);

                assertEquals(testClient, result);
                verify(clientValidator).validateCreate(newClient);
                verify(passwordService).generateSalt();
                verify(passwordService).hashPassword(PASSWORD, SALT);
                verify(clientRepository).save(newClient);
        }

        @Test
        void updateClient_Success() {
                // Prepare test data with existing client
                Client existingClient = Client.builder()
                                .id(CLIENT_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .password(HASHED_PASSWORD)
                                .salt(SALT)
                                .role(Role.CLIENT)
                                .billingDetails(testBillingDetails)
                                .build();

                // Client to update with same password (no password change)
                Client clientToUpdate = Client.builder()
                                .id(CLIENT_ID)
                                .username(USERNAME)
                                .email("newemail@example.com")
                                .password(HASHED_PASSWORD) // Using same hashed password
                                .role(Role.CLIENT)
                                .billingDetails(testBillingDetails)
                                .build();

                // Expected result after update
                Client expectedUpdatedClient = Client.builder()
                                .id(CLIENT_ID)
                                .username(USERNAME)
                                .email("newemail@example.com")
                                .password(HASHED_PASSWORD)
                                .salt(SALT)
                                .role(Role.CLIENT)
                                .billingDetails(testBillingDetails)
                                .build();

                // Mock repository behavior
                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(existingClient));
                when(clientRepository.save(argThat(client -> client.getId().equals(CLIENT_ID) &&
                                client.getUsername().equals(USERNAME) &&
                                client.getEmail().equals("newemail@example.com") &&
                                client.getPassword().equals(HASHED_PASSWORD) &&
                                client.getSalt().equals(SALT)))).thenReturn(expectedUpdatedClient);

                // Execute
                Client result = clientService.updateClient(clientToUpdate);

                // Verify
                assertNotNull(result);
                assertEquals(expectedUpdatedClient.getId(), result.getId());
                assertEquals(expectedUpdatedClient.getUsername(), result.getUsername());
                assertEquals(expectedUpdatedClient.getEmail(), result.getEmail());
                assertEquals(expectedUpdatedClient.getPassword(), result.getPassword());
                assertEquals(expectedUpdatedClient.getSalt(), result.getSalt());

                // Verify interactions
                verify(clientRepository).findById(CLIENT_ID);
                verify(clientValidator).validateUpdate(clientToUpdate);
                verify(clientRepository).save(argThat(client -> client.getId().equals(CLIENT_ID) &&
                                client.getUsername().equals(USERNAME) &&
                                client.getEmail().equals("newemail@example.com") &&
                                client.getPassword().equals(HASHED_PASSWORD) &&
                                client.getSalt().equals(SALT)));
                verifyNoMoreInteractions(clientRepository, passwordService);
        }

        @Test
        void updateClient_WithNewPassword_Success() {
                // Prepare test data
                Client existingClient = Client.builder()
                                .id(CLIENT_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .password(HASHED_PASSWORD)
                                .salt(SALT)
                                .role(Role.CLIENT)
                                .billingDetails(testBillingDetails)
                                .build();

                Client clientToUpdate = Client.builder()
                                .id(CLIENT_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .password("newpassword123")
                                .role(Role.CLIENT)
                                .billingDetails(testBillingDetails)
                                .build();

                String newSalt = "newSalt";
                String newHashedPassword = "newHashedPassword";

                // Mock behavior
                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(existingClient));
                when(passwordService.generateSalt()).thenReturn(newSalt);
                when(passwordService.hashPassword("newpassword123", newSalt)).thenReturn(newHashedPassword);
                when(clientRepository.save(argThat(client -> client.getId().equals(CLIENT_ID) &&
                                client.getPassword().equals(newHashedPassword) &&
                                client.getSalt().equals(newSalt)))).thenReturn(clientToUpdate);

                // Execute
                Client result = clientService.updateClient(clientToUpdate);

                // Verify
                assertNotNull(result);
                verify(clientRepository).findById(CLIENT_ID);
                verify(passwordService).generateSalt();
                verify(passwordService).hashPassword("newpassword123", newSalt);
                verify(clientValidator).validateUpdate(clientToUpdate);
                verify(clientRepository).save(argThat(client -> client.getId().equals(CLIENT_ID) &&
                                client.getPassword().equals(newHashedPassword) &&
                                client.getSalt().equals(newSalt)));
                verifyNoMoreInteractions(clientRepository, passwordService);
        }

        @Test
        void deleteClient_WithProfilePhoto_Success() {
                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(testClient));

                clientService.deleteClient(CLIENT_ID);

                verify(clientRepository).findById(CLIENT_ID);
                verify(clientRepository).deleteById(CLIENT_ID);
                verify(fileStorageService).deleteFile(PHOTO_PATH);
                verifyNoMoreInteractions(clientRepository, fileStorageService);
        }

        @Test
        void deleteClient_WithoutProfilePhoto_Success() {
                Client clientWithoutPhoto = Client.builder()
                                .id(CLIENT_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .build();

                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(clientWithoutPhoto));

                clientService.deleteClient(CLIENT_ID);

                verify(clientRepository).findById(CLIENT_ID);
                verify(clientRepository).deleteById(CLIENT_ID);
                verifyNoInteractions(fileStorageService);
        }

        @Test
        void deleteClient_FileStorageError_ThrowsException() {
                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(testClient));
                doThrow(new FileStorageException("File storage error"))
                                .when(fileStorageService).deleteFile(PHOTO_PATH);

                ClientServiceException exception = assertThrows(
                                ClientServiceException.class,
                                () -> clientService.deleteClient(CLIENT_ID));

                assertEquals("Client deleted but failed to delete profile photo: " + CLIENT_ID,
                                exception.getMessage());
                verify(clientRepository).findById(CLIENT_ID);
                verify(clientRepository).deleteById(CLIENT_ID);
                verify(fileStorageService).deleteFile(PHOTO_PATH);
        }

        @Test
        void getClientByUsername_NonExistentUsername_ThrowsException() {
                when(clientRepository.findByUsername(USERNAME))
                                .thenReturn(Optional.empty());

                ClientNotFoundException exception = assertThrows(
                                ClientNotFoundException.class,
                                () -> clientService.getClientByUsername(USERNAME));

                assertEquals("Client not found with username: " + USERNAME, exception.getMessage());
                verify(clientRepository).findByUsername(USERNAME);
                verifyNoMoreInteractions(clientRepository);
        }

        @Test
        void getClientByEmail_Success() {
                when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testClient));

                Client result = clientService.getClientByEmail(EMAIL);

                assertEquals(testClient, result);
                verify(clientRepository).findByEmail(EMAIL);
                verifyNoMoreInteractions(clientRepository);
        }

        @Test
        void getClientByEmail_NullEmail_ThrowsException() {
                InvalidClientDataException exception = assertThrows(
                                InvalidClientDataException.class,
                                () -> clientService.getClientByEmail(null));

                assertEquals("Email cannot be null", exception.getMessage());
                verifyNoInteractions(clientRepository);
        }

        @Test
        void createClient_DuplicateUsername_ThrowsException() {
                Client newClient = Client.builder()
                                .username(USERNAME)
                                .email("new@example.com")
                                .password(PASSWORD)
                                .role(Role.CLIENT)
                                .build();

                doThrow(new DuplicateClientException("Username already exists"))
                                .when(clientValidator).validateCreate(newClient);

                DuplicateClientException exception = assertThrows(
                                DuplicateClientException.class,
                                () -> clientService.createClient(newClient));

                assertEquals("Username already exists", exception.getMessage());
                verify(clientValidator).validateCreate(newClient);
                verifyNoInteractions(clientRepository);
        }

        @Test
        void updateClient_NonExistentClient_ThrowsException() {
                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());

                ClientNotFoundException exception = assertThrows(
                                ClientNotFoundException.class,
                                () -> clientService.updateClient(testClient));

                assertEquals("Client not found with id: " + CLIENT_ID, exception.getMessage());
                verify(clientRepository).findById(CLIENT_ID);
                verifyNoMoreInteractions(clientRepository);
        }

        @Test
        void updateClient_ValidationFails_ThrowsException() {
                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(testClient));
                doThrow(new InvalidClientDataException("Invalid client data"))
                                .when(clientValidator).validateUpdate(testClient);

                InvalidClientDataException exception = assertThrows(
                                InvalidClientDataException.class,
                                () -> clientService.updateClient(testClient));

                assertEquals("Invalid client data", exception.getMessage());
                verify(clientRepository).findById(CLIENT_ID);
                verify(clientValidator).validateUpdate(testClient);
                verifyNoMoreInteractions(clientRepository);
        }

        @Test
        void deleteClient_RepositoryError_ThrowsException() {
                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(testClient));
                doThrow(new RuntimeException("Database error"))
                                .when(clientRepository).deleteById(CLIENT_ID);

                ClientServiceException exception = assertThrows(
                                ClientServiceException.class,
                                () -> clientService.deleteClient(CLIENT_ID));

                assertEquals("Failed to delete client: " + CLIENT_ID, exception.getMessage());
                verify(clientRepository).findById(CLIENT_ID);
                verify(clientRepository).deleteById(CLIENT_ID);
                verifyNoInteractions(fileStorageService);
        }

        @Test
        void getAllClients_RepositoryError_ThrowsException() {
                when(clientRepository.findAll())
                                .thenThrow(new RuntimeException("Database error"));

                ClientServiceException exception = assertThrows(
                                ClientServiceException.class,
                                () -> clientService.getAllClients());

                assertEquals("Failed to retrieve all clients", exception.getMessage());
                verify(clientRepository).findAll();
        }

        @Test
        void getClient_NullId_ThrowsException() {
                InvalidClientDataException exception = assertThrows(
                                InvalidClientDataException.class,
                                () -> clientService.getClient(null));

                assertEquals("Client ID cannot be null", exception.getMessage());
                verifyNoInteractions(clientRepository);
        }

        @Test
        void getClient_RepositoryError_ThrowsException() {
                when(clientRepository.findById(CLIENT_ID)).thenThrow(new RuntimeException("Database error"));

                ClientServiceException exception = assertThrows(
                                ClientServiceException.class,
                                () -> clientService.getClient(CLIENT_ID));

                assertEquals("Error retrieving client with id: " + CLIENT_ID, exception.getMessage());
                verify(clientRepository).findById(CLIENT_ID);
        }

        @Test
        void getClientByUsername_NullUsername_ThrowsException() {
                InvalidClientDataException exception = assertThrows(
                                InvalidClientDataException.class,
                                () -> clientService.getClientByUsername(null));

                assertEquals("Username cannot be null", exception.getMessage());
                verifyNoInteractions(clientRepository);
        }

        @Test
        void getClientByUsername_RepositoryError_ThrowsException() {
                when(clientRepository.findByUsername(USERNAME)).thenThrow(new RuntimeException("Database error"));

                ClientServiceException exception = assertThrows(
                                ClientServiceException.class,
                                () -> clientService.getClientByUsername(USERNAME));

                assertEquals("Error retrieving client with username: " + USERNAME, exception.getMessage());
                verify(clientRepository).findByUsername(USERNAME);
        }

        @Test
        void getClientByEmail_RepositoryError_ThrowsException() {
                when(clientRepository.findByEmail(EMAIL)).thenThrow(new RuntimeException("Database error"));

                ClientServiceException exception = assertThrows(
                                ClientServiceException.class,
                                () -> clientService.getClientByEmail(EMAIL));

                assertEquals("Error retrieving client with email: " + EMAIL, exception.getMessage());
                verify(clientRepository).findByEmail(EMAIL);
        }

        @Test
        void getClientByEmail_NonExistentEmail_ThrowsException() {
                when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

                ClientNotFoundException exception = assertThrows(
                                ClientNotFoundException.class,
                                () -> clientService.getClientByEmail(EMAIL));

                assertEquals("Client not found with email: " + EMAIL, exception.getMessage());
                verify(clientRepository).findByEmail(EMAIL);
        }

        @Test
        void createClient_WithNonNullId_ThrowsException() {
                Client clientWithId = Client.builder()
                                .id(CLIENT_ID)
                                .username(USERNAME)
                                .email(EMAIL)
                                .password(PASSWORD)
                                .build();

                InvalidClientDataException exception = assertThrows(
                                InvalidClientDataException.class,
                                () -> clientService.createClient(clientWithId));

                assertEquals("Client ID must be null when creating a new client", exception.getMessage());
                verifyNoInteractions(clientRepository, passwordService);
        }

        @Test
        void createClient_NullClient_ThrowsException() {
                InvalidClientDataException exception = assertThrows(
                                InvalidClientDataException.class,
                                () -> clientService.createClient(null));

                assertEquals("Client cannot be null", exception.getMessage());
                verifyNoInteractions(clientRepository, passwordService);
        }

        @Test
        void createClient_RepositoryError_ThrowsException() {
                Client newClient = Client.builder()
                                .username(USERNAME)
                                .email(EMAIL)
                                .password(PASSWORD)
                                .build();

                when(passwordService.generateSalt()).thenReturn(SALT);
                when(passwordService.hashPassword(PASSWORD, SALT)).thenReturn(HASHED_PASSWORD);
                when(clientRepository.save(any(Client.class))).thenThrow(new RuntimeException("Database error"));

                ClientServiceException exception = assertThrows(
                                ClientServiceException.class,
                                () -> clientService.createClient(newClient));

                assertEquals("Failed to create client", exception.getMessage());
                verify(passwordService).generateSalt();
                verify(passwordService).hashPassword(PASSWORD, SALT);
                verify(clientValidator).validateCreate(newClient);
        }

        @Test
        void deleteClient_ClientNotFoundError_ThrowsException() {
                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());

                ClientNotFoundException exception = assertThrows(
                                ClientNotFoundException.class,
                                () -> clientService.deleteClient(CLIENT_ID));

                assertEquals("Client not found with id: " + CLIENT_ID, exception.getMessage());
                verify(clientRepository).findById(CLIENT_ID);
                verifyNoMoreInteractions(clientRepository, fileStorageService);
        }

        @Test
        void deleteClient_WithProfilePhoto_FileNotFoundError_ThrowsException() {
                when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(testClient));
                doThrow(new FileNotFoundException("File not found"))
                                .when(fileStorageService).deleteFile(PHOTO_PATH);

                ClientServiceException exception = assertThrows(
                                ClientServiceException.class,
                                () -> clientService.deleteClient(CLIENT_ID));

                assertEquals("Client deleted but failed to delete profile photo: " + CLIENT_ID, exception.getMessage());
                verify(clientRepository).findById(CLIENT_ID);
                verify(clientRepository).deleteById(CLIENT_ID);
                verify(fileStorageService).deleteFile(PHOTO_PATH);
        }
}