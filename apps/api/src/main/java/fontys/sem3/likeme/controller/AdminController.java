package fontys.sem3.likeme.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fontys.sem3.likeme.business.converter.user.admin.AdminConverter;
import fontys.sem3.likeme.business.interfaces.user.admin.AdminService;
import fontys.sem3.likeme.business.validator.user.admin.AdminValidator;
import fontys.sem3.likeme.controller.dto.admin.AdminResponse;
import fontys.sem3.likeme.controller.dto.admin.CreateAdminRequest;
import fontys.sem3.likeme.controller.dto.admin.UpdateAdminRequest;
import fontys.sem3.likeme.domain.user.admin.Admin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
@RolesAllowed("ADMIN")
@Tag(name = "Admin Controller", description = "Endpoints for admin management")
public class AdminController extends BaseController {
    private final AdminService adminService;
    private final AdminValidator adminValidator;

    @Operation(summary = "Create admin", description = "Create a new admin user")
    @PostMapping
    public ResponseEntity<AdminResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        Admin admin = adminService.createAdmin(AdminConverter.requestToDomain(request));
        return new ResponseEntity<>(AdminConverter.domainToResponse(admin), HttpStatus.CREATED);
    }

    @Operation(summary = "Get admin by ID", description = "Retrieve admin details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AdminResponse> getAdmin(@PathVariable Long id) {
        Admin admin = adminService.getAdminById(id);
        return ResponseEntity.ok(AdminConverter.domainToResponse(admin));
    }

    @Operation(summary = "Get all admins", description = "Retrieve all admin users")
    @GetMapping
    public ResponseEntity<List<AdminResponse>> getAllAdmins() {
        List<AdminResponse> responses = adminService.getAllAdmins().stream()
                .map(AdminConverter::domainToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Update admin", description = "Update admin details")
    @PutMapping("/{id}")
    public ResponseEntity<AdminResponse> updateAdmin(@PathVariable Long id,
            @Valid @RequestBody UpdateAdminRequest request) {
        validateOwnership(id);
        Admin existingAdmin = adminService.getAdminById(id);

        adminValidator.validateUpdate(request, existingAdmin);

        Admin admin = adminService.updateAdmin(AdminConverter.requestToDomain(request, id, existingAdmin));
        return ResponseEntity.ok(AdminConverter.domainToResponse(admin));
    }

    @Operation(summary = "Delete admin", description = "Delete an admin user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        validateOwnership(id);
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

}