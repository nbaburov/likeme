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

import fontys.sem3.likeme.business.converter.user.client.ClientConverter;
import fontys.sem3.likeme.business.interfaces.user.client.ClientService;
import fontys.sem3.likeme.business.validator.user.client.ClientValidator;
import fontys.sem3.likeme.controller.dto.client.ClientResponse;
import fontys.sem3.likeme.controller.dto.client.CreateClientRequest;
import fontys.sem3.likeme.controller.dto.client.UpdateClientRequest;
import fontys.sem3.likeme.domain.user.client.Client;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Client Controller", description = "Endpoints for client management")
public class ClientController extends BaseController {
    private final ClientService clientService;
    private final ClientValidator clientValidator;

    @Operation(summary = "Create client", description = "Create a new client user")
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody CreateClientRequest request) {
        Client client = ClientConverter.requestToDomain(request);
        Client createdClient = clientService.createClient(client);
        return new ResponseEntity<>(ClientConverter.toResponse(createdClient), HttpStatus.CREATED);
    }

    @Operation(summary = "Get client by ID", description = "Retrieve client details by ID")
    @GetMapping("/{id}")
    @RolesAllowed({"ADMIN", "CLIENT", "INFLUENCER"})
    public ResponseEntity<ClientResponse> getClient(@PathVariable Long id) {
        return ResponseEntity.ok(ClientConverter.toResponse(clientService.getClient(id)));
    }

    @Operation(summary = "Get all clients", description = "Retrieve all client users")
    @GetMapping
    @RolesAllowed({"ADMIN", "INFLUENCER"})
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<ClientResponse> responses = clientService.getAllClients().stream()
                .map(ClientConverter::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Update client", description = "Update client details")
    @PutMapping("/{id}")
    @RolesAllowed({"ADMIN", "CLIENT"})
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClientRequest request) {
        validateOwnership(id);
        Client existingClient = clientService.getClient(id);

       clientValidator.validateUpdate(request, existingClient);

        Client clientToUpdate = ClientConverter.requestToDomain(request, id, existingClient);
        Client updatedClient = clientService.updateClient(clientToUpdate);
        
        return ResponseEntity.ok(ClientConverter.toResponse(updatedClient));
    }

    @Operation(summary = "Delete client", description = "Delete a client user")
    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN", "CLIENT"})
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        validateOwnership(id);
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

}