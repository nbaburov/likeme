package fontys.sem3.likeme.business.interfaces.utils;

public interface EmailService {
    void sendApprovalEmail(String to, String username, String setupToken);
}