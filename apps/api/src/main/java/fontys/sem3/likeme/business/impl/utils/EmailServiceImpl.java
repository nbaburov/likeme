package fontys.sem3.likeme.business.impl.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import fontys.sem3.likeme.business.exception.utils.email.EmailSendingException;
import fontys.sem3.likeme.business.interfaces.utils.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final SendGrid sendGrid;

    @Value("${sendgrid.api-key:}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void sendApprovalEmail(String to, String username, String setupToken) {
        if (to == null || username == null || setupToken == null) {
            throw new IllegalArgumentException("Email parameters cannot be null");
        }

        try {
            Email from = new Email(fromEmail);
            Email toEmail = new Email(to);
            String subject = "Your LikeMe Influencer Application is Approved!";
            String setupUrl = frontendUrl + "/influencers/setup?token=" + setupToken;

            // Without a SendGrid key (local/demo runs) the setup link is logged so onboarding still works.
            if (apiKey == null || apiKey.isBlank()) {
                log.info("SendGrid not configured; approval email for {} <{}>: {}", username, to, setupUrl);
                return;
            }

            Content content = new Content("text/html",
                    "<h1>Congratulations " + username + "!</h1>" +
                            "<p>Your application to become an influencer has been approved.</p>" +
                            "<p>Please complete your profile by clicking the button below:</p>" +
                            "<p>This link will expire in 24 hours.</p>" +
                            "<a href='" + setupUrl
                            + "' style='background-color: #4CAF50; color: white; padding: 14px 20px; text-decoration: none; border-radius: 4px; margin-top: 20px; display: inline-block;'>Complete Profile</a>");

            Mail mail = new Mail(from, subject, toEmail, content);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sendGrid.api(request);
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send approval email", e);
        }
    }
}
