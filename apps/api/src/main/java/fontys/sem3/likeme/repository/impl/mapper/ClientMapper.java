package fontys.sem3.likeme.repository.impl.mapper;

import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.repository.impl.entity.user.client.ClientEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientMapper {
    public static ClientEntity mapToEntity(Client client) {
        return ClientEntity.builder()
                .id(client.getId())
                .username(client.getUsername())
                .email(client.getEmail())
                .password(client.getPassword())
                .salt(client.getSalt())
                .profilePhotoPath(client.getProfilePhotoPath())
                .instagramHandle(client.getInstagramHandle())
                .isInstagramConnected(client.getIsInstagramConnected())
                .instagramAccessToken(client.getInstagramAccessToken())
                .billingDetails(BillingDetailsMapper.mapToEntityBillingDetails(client.getBillingDetails()))
                .isActive(client.getIsActive())
                .createdOn(client.getCreatedOn())
                .updatedOn(client.getUpdatedOn())
                .lastLoginOn(client.getLastLoginOn())
                .build();
    }

    public static Client mapToDomain(ClientEntity entity) {
        return Client.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .salt(entity.getSalt())
                .role(Role.CLIENT)
                .profilePhotoPath(entity.getProfilePhotoPath())
                .instagramHandle(entity.getInstagramHandle())
                .isInstagramConnected(entity.getIsInstagramConnected())
                .instagramAccessToken(entity.getInstagramAccessToken())
                .billingDetails(BillingDetailsMapper.mapToDomainBillingDetails(entity.getBillingDetails()))
                .isActive(entity.getIsActive())
                .createdOn(entity.getCreatedOn())
                .updatedOn(entity.getUpdatedOn())
                .lastLoginOn(entity.getLastLoginOn())
                .build();
    }
}
