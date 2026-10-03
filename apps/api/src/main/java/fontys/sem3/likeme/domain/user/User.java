package fontys.sem3.likeme.domain.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class User {
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;
    @EqualsAndHashCode.Include
    private String username;
    @EqualsAndHashCode.Include
    private String email;
    @EqualsAndHashCode.Include
    private String password;
    @EqualsAndHashCode.Include
    private String salt;
    @EqualsAndHashCode.Include
    private Role role;
    @EqualsAndHashCode.Include
    private String profilePhotoPath;
    @Builder.Default
    private Date createdOn = new Date();
    @Builder.Default
    private Date updatedOn = new Date();
    @Builder.Default
    @EqualsAndHashCode.Include
    private Boolean isActive = true;
    @EqualsAndHashCode.Include
    private Date lastLoginOn;
}
