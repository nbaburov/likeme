package fontys.sem3.likeme.repository.impl.entity.user.client;

import java.util.Date;

import fontys.sem3.likeme.repository.impl.entity.user.BillingDetailsEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    @Column(name = "profile_photo_path", nullable = false)
    private String profilePhotoPath;

    @Column(name = "instagram_handle", nullable = false)
    private String instagramHandle;

    @Column(name = "is_instagram_connected", nullable = false)
    private Boolean isInstagramConnected;

    @Column(name = "instagram_access_token")
    private String instagramAccessToken;

    @Embedded
    private BillingDetailsEntity billingDetails;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @Column(name = "last_login_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginOn;
}
