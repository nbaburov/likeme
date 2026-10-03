package fontys.sem3.likeme.business.exception.security.setuptoken;

public class SetupTokenServiceException extends RuntimeException {
  public SetupTokenServiceException(String message) {
    super(message);
  }

  public SetupTokenServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
