package fontys.sem3.likeme.business.interfaces.user.admin;

import java.util.List;

import fontys.sem3.likeme.domain.user.admin.Admin;

public interface AdminService {
    Admin createAdmin(Admin admin);

    Admin getAdminById(Long id);

    List<Admin> getAllAdmins();

    Admin updateAdmin(Admin admin);

    void deleteAdmin(Long id);

    Admin getAdminByUsername(String username);
}
