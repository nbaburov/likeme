package fontys.sem3.likeme.controller;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
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

import fontys.sem3.likeme.business.converter.user.admin.AdminConverter;
import fontys.sem3.likeme.business.interfaces.user.admin.AdminService;
import fontys.sem3.likeme.business.interfaces.security.jwt.JwtService;
import fontys.sem3.likeme.controller.dto.admin.CreateAdminRequest;
import fontys.sem3.likeme.controller.dto.admin.UpdateAdminRequest;
import fontys.sem3.likeme.domain.security.jwt.AccessToken;
import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.domain.user.admin.Permissions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JwtService jwtService;

    private Admin testAdmin;
    private String jwtToken;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @BeforeEach
    void setUp() {
        testAdmin = Admin.builder()
                .id(1L)
                .username("testadmin")
                .email("test@admin.com")
                .permissions(Permissions.FULL)
                .profilePhotoPath("/photos/admin.jpg")
                .isActive(true)
                .createdOn(new Date())
                .updatedOn(new Date())
                .lastLoginOn(new Date())
                .build();

        AccessToken accessToken = AccessToken.builder()
                .token("test-jwt-token")
                .subject(testAdmin.getUsername())
                .userId(testAdmin.getId())
                .role(Role.ADMIN)
                .build();

        jwtToken = accessToken.getToken();
        when(jwtService.isTokenValid(jwtToken)).thenReturn(true);
        when(jwtService.decodeToken(jwtToken)).thenReturn(accessToken);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAdmin_ValidRequest_ReturnsCreated() throws Exception {
        CreateAdminRequest createRequest = createValidCreateRequest();
        Admin adminToCreate = AdminConverter.requestToDomain(createRequest);
        when(adminService.createAdmin(adminToCreate)).thenReturn(testAdmin);

        mockMvc.perform(post("/admins")
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testAdmin.getId()))
                .andExpect(jsonPath("$.username").value(testAdmin.getUsername()))
                .andExpect(jsonPath("$.email").value(testAdmin.getEmail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdmin_ExistingId_ReturnsAdmin() throws Exception {
        when(adminService.getAdminById(testAdmin.getId())).thenReturn(testAdmin);

        mockMvc.perform(get("/admins/1")
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAdmin.getId()))
                .andExpect(jsonPath("$.username").value(testAdmin.getUsername()))
                .andExpect(jsonPath("$.email").value(testAdmin.getEmail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllAdmins_ReturnsAdminList() throws Exception {
        when(adminService.getAllAdmins()).thenReturn(List.of(testAdmin));

        mockMvc.perform(get("/admins")
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testAdmin.getId()))
                .andExpect(jsonPath("$[0].username").value(testAdmin.getUsername()))
                .andExpect(jsonPath("$[0].email").value(testAdmin.getEmail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAdmin_ValidRequest_ReturnsUpdatedAdmin() throws Exception {
        UpdateAdminRequest updateRequest = createValidUpdateRequest();
        when(adminService.getAdminById(testAdmin.getId())).thenReturn(testAdmin);
        Admin adminToUpdate = AdminConverter.requestToDomain(updateRequest, 1L, testAdmin);
        when(adminService.updateAdmin(adminToUpdate)).thenReturn(testAdmin);

        mockMvc.perform(put("/admins/1")
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAdmin.getId()))
                .andExpect(jsonPath("$.username").value(testAdmin.getUsername()))
                .andExpect(jsonPath("$.email").value(testAdmin.getEmail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAdmin_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/admins/1")
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void accessWithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/admins"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void accessWithWrongRole_ReturnsForbidden() throws Exception {
        AccessToken clientAccessToken = AccessToken.builder()
                .token("test-client-jwt-token")
                .subject("testclient")
                .userId(2L)
                .role(Role.CLIENT)
                .build();

        String clientToken = clientAccessToken.getToken();
        when(jwtService.isTokenValid(clientToken)).thenReturn(true);
        when(jwtService.decodeToken(clientToken)).thenReturn(clientAccessToken);

        mockMvc.perform(get("/admins")
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + clientToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private CreateAdminRequest createValidCreateRequest() {
        return CreateAdminRequest.builder()
                .username("newadmin")
                .email("new@admin.com")
                .permissions(Permissions.FULL)
                .password("Test123!@#")
                .profilePhotoPath("/photos/new.jpg")
                .build();
    }

    private UpdateAdminRequest createValidUpdateRequest() {
        return UpdateAdminRequest.builder()
                .username("updatedadmin")
                .email("updated@admin.com")
                .permissions(Permissions.FULL)
                .isActive(true)
                .profilePhotoPath("/photos/updated.jpg")
                .build();
    }
}