package fontys.sem3.likeme.business.impl.user.client;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fontys.sem3.likeme.business.exception.user.client.ClientNotFoundException;
import fontys.sem3.likeme.business.exception.user.client.ClientServiceException;
import fontys.sem3.likeme.business.exception.user.client.DuplicateClientException;
import fontys.sem3.likeme.business.exception.user.client.InvalidClientDataException;
import fontys.sem3.likeme.business.exception.utils.file.FileNotFoundException;
import fontys.sem3.likeme.business.exception.utils.file.FileStorageException;
import fontys.sem3.likeme.business.interfaces.security.PasswordService;
import fontys.sem3.likeme.business.interfaces.user.client.ClientService;
import fontys.sem3.likeme.business.interfaces.utils.FileStorageService;
import fontys.sem3.likeme.business.validator.user.client.ClientValidator;
import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.repository.interfaces.client.ClientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientValidator clientValidator;
    private final PasswordService passwordService;
    private final FileStorageService fileStorageService;

    @Override
    public List<Client> getAllClients() {
        try {
            return clientRepository.findAll();
        } catch (Exception e) {
            throw new ClientServiceException("Failed to retrieve all clients", e);
        }
    }

    @Override
    public Client getClient(Long id) {
        if (id == null) {
            throw new InvalidClientDataException("Client ID cannot be null");
        }
        try {
            return clientRepository.findById(id)
                    .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));
        } catch (ClientNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ClientServiceException("Error retrieving client with id: " + id, e);
        }
    }

    @Override
    public Client getClientByUsername(String username) {
        if (username == null) {
            throw new InvalidClientDataException("Username cannot be null");
        }
        try {
            return clientRepository.findByUsername(username)
                    .orElseThrow(() -> new ClientNotFoundException("Client not found with username: " + username));
        } catch (ClientNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ClientServiceException("Error retrieving client with username: " + username, e);
        }
    }

    @Override
    public Client getClientByEmail(String email) {
        if (email == null) {
            throw new InvalidClientDataException("Email cannot be null");
        }
        try {
            return clientRepository.findByEmail(email)
                    .orElseThrow(() -> new ClientNotFoundException("Client not found with email: " + email));
        } catch (ClientNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ClientServiceException("Error retrieving client with email: " + email, e);
        }
    }

    @Transactional
    @Override
    public Client createClient(Client client) {
        if (client == null) {
            throw new InvalidClientDataException("Client cannot be null");
        }
        if (client.getId() != null) {
            throw new InvalidClientDataException("Client ID must be null when creating a new client");
        }

        try {

            String salt = passwordService.generateSalt();
            client.setSalt(salt);
            client.setPassword(passwordService.hashPassword(client.getPassword(), salt));
            clientValidator.validateCreate(client);

            return clientRepository.save(client);
        } catch (DuplicateClientException | InvalidClientDataException e) {
            throw e;
        } catch (Exception e) {
            throw new ClientServiceException("Failed to create client", e);
        }
    }

    @Transactional
    @Override
    public Client updateClient(Client client) {
        if (client == null || client.getId() == null) {
            throw new InvalidClientDataException("Client and ID cannot be null");
        }

        try {
            Client existingClient = getClient(client.getId());

            if (!existingClient.getPassword().equals(client.getPassword())) {
                String salt = passwordService.generateSalt();
                client.setSalt(salt);
                client.setPassword(passwordService.hashPassword(client.getPassword(), salt));
            } else {
                client.setSalt(existingClient.getSalt());
            }

            clientValidator.validateUpdate(client);
            return clientRepository.save(client);
        } catch (ClientNotFoundException | InvalidClientDataException | DuplicateClientException e) {
            throw e;
        } catch (Exception e) {
            throw new ClientServiceException("Failed to update client with id: " + client.getId(), e);
        }
    }

    @Override
    public void deleteClient(Long id) {
        Client client = getClient(id);

        if (client.getProfilePhotoPath() != null && !client.getProfilePhotoPath().isEmpty()) {
            try {
                clientRepository.deleteById(id);
                fileStorageService.deleteFile(client.getProfilePhotoPath());
            } catch (FileNotFoundException | FileStorageException e) {
                throw new ClientServiceException("Client deleted but failed to delete profile photo: " + id);
            } catch (ClientNotFoundException e) {
                throw e;
            } catch (Exception e) {
                throw new ClientServiceException("Failed to delete client: " + id, e);
            }
        } else {
            try {
                clientRepository.deleteById(id);
            } catch (ClientNotFoundException e) {
                throw e;
            } catch (Exception e) {
                throw new ClientServiceException("Failed to delete client: " + id, e);
            }
        }
    }
}