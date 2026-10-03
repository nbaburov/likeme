package fontys.sem3.likeme.business.validator.user.client;

import org.springframework.stereotype.Component;

import fontys.sem3.likeme.business.converter.user.client.ClientConverter;
import fontys.sem3.likeme.business.exception.user.client.DuplicateClientException;
import fontys.sem3.likeme.business.exception.user.client.InvalidClientDataException;
import fontys.sem3.likeme.business.validator.security.PasswordValidator;
import fontys.sem3.likeme.controller.dto.client.UpdateClientRequest;
import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.repository.interfaces.client.ClientRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClientValidator {
    private final ClientRepository clientRepository;
    private final PasswordValidator passwordValidator;

    public void validateCreate(Client client) {
        if (clientRepository.existsByUsername(client.getUsername())) {
            throw new DuplicateClientException("Username already exists");
        }
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new DuplicateClientException("Email already exists");
        }
    }

    public void validateUpdate(Client client) {
        clientRepository.findById(client.getId()).ifPresent(existingClient -> {
            if (!existingClient.getUsername().equals(client.getUsername()) 
                    && clientRepository.findByUsername(client.getUsername()).isPresent()) {
                throw new DuplicateClientException("Username already exists");
            }
            if (!existingClient.getEmail().equals(client.getEmail()) 
                    && clientRepository.findByEmail(client.getEmail()).isPresent()) {
                throw new DuplicateClientException("Email already exists");
            }
        });
    }

    public void validateUpdate(UpdateClientRequest request, Client existingClient) {
        if (request == null || (request.getUsername() == null && request.getEmail() == null
                && request.getProfilePhotoPath() == null && request.getIsActive() == null 
                && request.getPassword() == null && request.getInstagramHandle() == null
                && request.getBillingDetails() == null && request.getIsInstagramConnected() == null && request.getInstagramAccessToken() == null)) {
            throw new InvalidClientDataException("Update request cannot be empty");
        }

        // Check for no changes
        Client updatedClient = ClientConverter.requestToDomain(request, existingClient.getId(), existingClient);
        if (existingClient.equals(updatedClient) && passwordValidator.comparePasswords(
                request.getPassword(), existingClient.getPassword(), existingClient.getSalt())) {
            throw new InvalidClientDataException("No changes detected for client with id: " + existingClient.getId());
        }
    }
} 