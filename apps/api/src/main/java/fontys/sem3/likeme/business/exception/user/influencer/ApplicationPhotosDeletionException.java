package fontys.sem3.likeme.business.exception.user.influencer;

import lombok.Getter;

@Getter
public class ApplicationPhotosDeletionException extends RuntimeException {
    private final boolean applicationDeleted;
    private final boolean profilePhotoDeleted;
    private final boolean coverPhotoDeleted;

    public ApplicationPhotosDeletionException(String message,
            boolean applicationDeleted,
            boolean profilePhotoDeleted,
            boolean coverPhotoDeleted) {
        super(message);
        this.applicationDeleted = applicationDeleted;
        this.profilePhotoDeleted = profilePhotoDeleted;
        this.coverPhotoDeleted = coverPhotoDeleted;
    }
}
