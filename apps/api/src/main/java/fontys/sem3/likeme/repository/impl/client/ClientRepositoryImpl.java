package fontys.sem3.likeme.repository.impl.client;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.repository.impl.mapper.ClientMapper;
import fontys.sem3.likeme.repository.interfaces.client.ClientRepository;
import fontys.sem3.likeme.repository.jpa.client.ClientRepositoryJPA;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {
    private final ClientRepositoryJPA clientRepositoryJPA;

    @Override
    public Client save(Client client) {
        return ClientMapper.mapToDomain(
                clientRepositoryJPA.save(
                        ClientMapper.mapToEntity(client)));
    }

    @Override
    public List<Client> findAll() {
        return clientRepositoryJPA.findAll()
                .stream()
                .map(ClientMapper::mapToDomain)
                .toList();
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepositoryJPA.findById(id)
                .map(ClientMapper::mapToDomain);
    }

    @Override
    public Optional<Client> findByUsername(String username) {
        return clientRepositoryJPA.findByUsername(username)
                .map(ClientMapper::mapToDomain);
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        return clientRepositoryJPA.findByEmail(email)
                .map(ClientMapper::mapToDomain);
    }

    @Override
    public void deleteById(Long id) {
        clientRepositoryJPA.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return clientRepositoryJPA.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return clientRepositoryJPA.existsByEmail(email);
    }
}
