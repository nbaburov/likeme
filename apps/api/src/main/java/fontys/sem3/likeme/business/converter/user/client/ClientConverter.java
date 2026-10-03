package fontys.sem3.likeme.business.converter.user.client;

import fontys.sem3.likeme.business.converter.user.billing.BillingDetailsConverter;
import fontys.sem3.likeme.business.exception.InvalidRequest;
import fontys.sem3.likeme.business.exception.user.client.InvalidClientConversionException;
import fontys.sem3.likeme.controller.dto.client.ClientResponse;
import fontys.sem3.likeme.controller.dto.client.CreateClientRequest;
import fontys.sem3.likeme.controller.dto.client.UpdateClientRequest;
import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.client.Client;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientConverter {

    public static Client requestToDomain(CreateClientRequest request) {
        if (request == null) {
            throw new InvalidRequest("Client request cannot be null");
        }

        try {
            return Client.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .role(Role.CLIENT)
                    .profilePhotoPath(request.getProfilePhotoPath())
                    .instagramHandle(request.getInstagramHandle())
                    .isInstagramConnected(false)
                    .billingDetails(BillingDetailsConverter.toBillingDetails(request.getBillingDetails()))
                    .isActive(true)
                    .build();
        } catch (Exception e) {
            throw new InvalidClientConversionException(
                    "Failed to convert create request to client domain: " + e.getMessage(), e);
        }
    }

    public static Client requestToDomain(UpdateClientRequest request, Long id, Client existingClient) {
        if (request == null || existingClient == null) {
            throw new InvalidRequest("Update request and existing client cannot be null");
        }

        try {
            return Client.builder()
                    .id(id)
                    .username(request.getUsername() != null ? request.getUsername() : existingClient.getUsername())
                    .email(request.getEmail() != null ? request.getEmail() : existingClient.getEmail())
                    .password(request.getPassword() != null ? request.getPassword() : existingClient.getPassword())
                    .salt(existingClient.getSalt())
                    .role(Role.CLIENT)
                    .profilePhotoPath(request.getProfilePhotoPath() != null ? request.getProfilePhotoPath()
                            : existingClient.getProfilePhotoPath())
                    .instagramHandle(request.getInstagramHandle() != null ? request.getInstagramHandle()
                            : existingClient.getInstagramHandle())
                    .isInstagramConnected(request.getIsInstagramConnected() != null ? request.getIsInstagramConnected()
                            : existingClient.getIsInstagramConnected())
                    .instagramAccessToken(request.getInstagramAccessToken() != null ? request.getInstagramAccessToken()
                            : existingClient.getInstagramAccessToken())
                    .billingDetails(
                            request.getBillingDetails() != null
                                    ? BillingDetailsConverter.toBillingDetails(request.getBillingDetails(),
                                            existingClient.getBillingDetails())
                                    : existingClient.getBillingDetails())
                    .isActive(request.getIsActive() != null ? request.getIsActive() : existingClient.getIsActive())
                    .createdOn(existingClient.getCreatedOn())
                    .updatedOn(existingClient.getUpdatedOn())
                    .lastLoginOn(existingClient.getLastLoginOn())
                    .build();
        } catch (Exception e) {
            throw new InvalidClientConversionException(
                    "Failed to convert update request to client domain: " + e.getMessage(), e);
        }
    }

    public static ClientResponse toResponse(Client client) {
        if (client == null) {
            throw new InvalidRequest("Client cannot be null");
        }

        try {
            return ClientResponse.builder()
                    .id(client.getId())
                    .username(client.getUsername())
                    .email(client.getEmail())
                    .profilePhotoPath(client.getProfilePhotoPath())
                    .instagramHandle(client.getInstagramHandle())
                    .isInstagramConnected(client.getIsInstagramConnected())
                    .instagramAccessToken(client.getInstagramAccessToken())
                    .billingDetails(BillingDetailsConverter.toBillingDetailsResponse(client.getBillingDetails()))
                    .isActive(client.getIsActive())
                    .createdOn(client.getCreatedOn())
                    .updatedOn(client.getUpdatedOn())
                    .lastLoginOn(client.getLastLoginOn())
                    .build();
        } catch (Exception e) {
            throw new InvalidClientConversionException(
                    "Failed to convert client to response: " + e.getMessage(), e);
        }
    }
}