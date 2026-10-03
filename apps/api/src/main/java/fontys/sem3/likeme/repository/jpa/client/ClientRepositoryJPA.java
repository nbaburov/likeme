package fontys.sem3.likeme.repository.jpa.client;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fontys.sem3.likeme.repository.impl.entity.user.client.ClientEntity;

@Repository
public interface ClientRepositoryJPA extends JpaRepository<ClientEntity, Long> {
    Optional<ClientEntity> findByUsername(String username);
    Optional<ClientEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
