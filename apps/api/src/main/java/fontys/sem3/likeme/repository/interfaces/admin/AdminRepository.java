package fontys.sem3.likeme.repository.interfaces.admin;

import fontys.sem3.likeme.domain.user.admin.Admin;
import java.util.List;
import java.util.Optional;

public interface AdminRepository {
    Admin save(Admin admin);
    Optional<Admin> findById(Long id);
    List<Admin> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Admin> findByUsername(String username);
    Optional<Admin> findByEmail(String email);
}
