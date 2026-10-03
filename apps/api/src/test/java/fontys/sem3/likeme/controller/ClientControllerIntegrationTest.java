package fontys.sem3.likeme.controller;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import fontys.sem3.likeme.business.converter.user.client.ClientConverter;
import fontys.sem3.likeme.business.interfaces.security.jwt.JwtService;
import fontys.sem3.likeme.business.interfaces.user.client.ClientService;
import fontys.sem3.likeme.business.validator.user.client.ClientValidator;
import fontys.sem3.likeme.controller.dto.billing.CreateBillingDetailsRequest;
import fontys.sem3.likeme.controller.dto.client.CreateClientRequest;
import fontys.sem3.likeme.controller.dto.client.UpdateClientRequest;
import fontys.sem3.likeme.domain.security.jwt.AccessToken;
import fontys.sem3.likeme.domain.user.BillingDetails;
import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.client.Client;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClientControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private ClientService clientService;

        @MockitoBean
        private ClientValidator clientValidator;

        @MockitoBean
        private JwtService jwtService;

        private static final String AUTHORIZATION_HEADER = "Authorization";
        private static final String BEARER_PREFIX = "Bearer ";
        private String jwtToken;
        private Client testClient;

        @BeforeEach
        void setUp() {
                BillingDetails billingDetails = BillingDetails.builder()
                                .firstName("John")
                                .lastName("Doe")
                                .streetAddress("Test Street")
                                .city("Test City")
                                .country("Test Country")
                                .state("Test State")
                                .zipCode("1234AB")
                                .build();

                testClient = Client.builder()
                                .id(1L)
                                .username("testclient")
                                .email("test@client.com")
                                .profilePhotoPath("/photos/client.jpg")
                                .instagramHandle("testhandle")
                                .isInstagramConnected(false)
                                .billingDetails(billingDetails)
                                .isActive(true)
                                .createdOn(new Date())
                                .updatedOn(new Date())
                                .lastLoginOn(new Date())
                                .build();

                AccessToken accessToken = AccessToken.builder()
                                .token("test-jwt-token")
                                .subject(testClient.getUsername())
                                .userId(testClient.getId())
                                .role(Role.CLIENT)
                                .build();

                jwtToken = accessToken.getToken();
                when(jwtService.isTokenValid(jwtToken)).thenReturn(true);
                when(jwtService.decodeToken(jwtToken)).thenReturn(accessToken);
        }

        @Test
        @WithAnonymousUser
        void createClient_ValidRequest_ReturnsCreated() throws Exception {
                CreateClientRequest createRequest = createValidCreateRequest();
                Client clientToCreate = ClientConverter.requestToDomain(createRequest);
                when(clientService.createClient(clientToCreate)).thenReturn(testClient);
                doNothing().when(clientValidator).validateCreate(clientToCreate);

                mockMvc.perform(post("/clients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(testClient.getId()))
                                .andExpect(jsonPath("$.username").value(testClient.getUsername()))
                                .andExpect(jsonPath("$.email").value(testClient.getEmail()))
                                .andExpect(jsonPath("$.instagramHandle").value(testClient.getInstagramHandle()))
                                .andExpect(jsonPath("$.isInstagramConnected")
                                                .value(testClient.getIsInstagramConnected()))
                                .andExpect(jsonPath("$.billingDetails").exists());
        }

        @Test
        void createClient_InvalidRequest_ReturnsBadRequest() throws Exception {
                CreateClientRequest invalidRequest = CreateClientRequest.builder()
                                .username("")
                                .email("invalid-email")
                                .password("weak")
                                .build();

                mockMvc.perform(post("/clients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @WithMockUser(roles = { "CLIENT" })
        void getClient_ExistingId_ReturnsClient() throws Exception {
                when(clientService.getClient(1L)).thenReturn(testClient);

                mockMvc.perform(get("/clients/1")
                                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(testClient.getId()))
                                .andExpect(jsonPath("$.username").value(testClient.getUsername()))
                                .andExpect(jsonPath("$.email").value(testClient.getEmail()));
        }

        @Test
        @WithMockUser(roles = { "ADMIN" })
        void getAllClients_ReturnsClientList() throws Exception {
                // Create test JWT token with ADMIN role
                AccessToken adminAccessToken = AccessToken.builder()
                                .token("test-jwt-token")
                                .subject("testadmin")
                                .userId(1L)
                                .role(Role.ADMIN)
                                .build();

                when(jwtService.isTokenValid(jwtToken)).thenReturn(true);
                when(jwtService.decodeToken(jwtToken)).thenReturn(adminAccessToken);
                when(clientService.getAllClients()).thenReturn(List.of(testClient));

                mockMvc.perform(get("/clients")
                                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(testClient.getId()))
                                .andExpect(jsonPath("$[0].username").value(testClient.getUsername()))
                                .andExpect(jsonPath("$[0].email").value(testClient.getEmail()));
        }

        @Test
        @WithMockUser(roles = { "CLIENT" })
        void updateClient_ValidRequest_ReturnsUpdatedClient() throws Exception {
                // Create test JWT token with CLIENT role and matching userId
                AccessToken mockToken = AccessToken.builder()
                                .token("test-jwt-token")
                                .subject("testclient")
                                .userId(1L)
                                .role(Role.CLIENT)
                                .build();

                when(jwtService.isTokenValid(jwtToken)).thenReturn(true);
                when(jwtService.decodeToken(jwtToken)).thenReturn(mockToken);

                UpdateClientRequest updateRequest = createValidUpdateRequest();
                when(clientService.getClient(1L)).thenReturn(testClient);
                Client clientToUpdate = ClientConverter.requestToDomain(updateRequest, 1L, testClient);
                when(clientService.updateClient(clientToUpdate)).thenReturn(testClient);
                doNothing().when(clientValidator).validateUpdate(updateRequest, testClient);

                mockMvc.perform(put("/clients/1")
                                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(testClient.getId()))
                                .andExpect(jsonPath("$.username").value(testClient.getUsername()))
                                .andExpect(jsonPath("$.email").value(testClient.getEmail()));
        }

        @Test
        @WithMockUser(roles = { "CLIENT" })
        void deleteClient_ExistingId_ReturnsNoContent() throws Exception {
                doNothing().when(clientService).deleteClient(1L);

                mockMvc.perform(delete("/clients/1")
                                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtToken))
                                .andExpect(status().isNoContent());
        }

        @Test
        void accessWithoutAuth_ReturnsUnauthorized() throws Exception {
                mockMvc.perform(get("/clients"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @WithMockUser(roles = { "CLIENT" })
        void accessWithWrongRole_ReturnsForbidden() throws Exception {
                mockMvc.perform(get("/clients")
                                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtToken))
                                .andExpect(status().isForbidden())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        private CreateClientRequest createValidCreateRequest() {
                return CreateClientRequest.builder()
                                .username("newclient")
                                .email("new@client.com")
                                .password("Test123!@#")
                                .profilePhotoPath("/photos/new.jpg")
                                .instagramHandle("newhandle")
                                .billingDetails(CreateBillingDetailsRequest.builder()
                                                .firstName("John")
                                                .lastName("Doe")
                                                .streetAddress("Test Street")
                                                .city("Test City")
                                                .country("Test Country")
                                                .state("Test State")
                                                .zipCode("1234AB")
                                                .build())
                                .build();
        }

        private UpdateClientRequest createValidUpdateRequest() {
                return UpdateClientRequest.builder()
                                .username("updatedclient")
                                .email("updated@client.com")
                                .password("UpdatedTest123!@#")
                                .profilePhotoPath("/photos/updated.jpg")
                                .instagramHandle("updatedhandle")
                                .isActive(true)
                                .build();
        }
}