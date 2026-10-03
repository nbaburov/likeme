package fontys.sem3.likeme.repository.jpa.admin;

import fontys.sem3.likeme.repository.impl.entity.user.admin.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminRepositoryJPA extends JpaRepository<AdminEntity, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<AdminEntity> findByUsername(String username);
    Optional<AdminEntity> findByEmail(String email);
}
