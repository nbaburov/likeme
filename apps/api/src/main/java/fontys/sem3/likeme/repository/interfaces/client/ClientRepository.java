package fontys.sem3.likeme.repository.interfaces.client;

import java.util.List;
import java.util.Optional;

import fontys.sem3.likeme.domain.user.client.Client;

public interface ClientRepository {
    Client save(Client client);
    List<Client> findAll();
    Optional<Client> findById(Long id);
    Optional<Client> findByUsername(String username);
    Optional<Client> findByEmail(String email);
    void deleteById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
