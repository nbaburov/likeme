package fontys.sem3.likeme.business.impl.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import fontys.sem3.likeme.business.exception.utils.email.EmailSendingException;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {
    @Mock
    private SendGrid sendGrid;

    @InjectMocks
    private EmailServiceImpl emailService;

    private static final String FROM_EMAIL = "test@likeme.com";
    private static final String FRONTEND_URL = "http://localhost:3000";
    private static final String TO_EMAIL = "user@example.com";
    private static final String USERNAME = "testuser";
    private static final String SETUP_TOKEN = "valid_setup_token";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
        ReflectionTestUtils.setField(emailService, "frontendUrl", FRONTEND_URL);
        ReflectionTestUtils.setField(emailService, "apiKey", "SG.test-key");
    }

    @Test
    void sendApprovalEmail_NoApiKey_LogsInsteadOfSending() {
        ReflectionTestUtils.setField(emailService, "apiKey", "");

        assertDoesNotThrow(() -> emailService.sendApprovalEmail(TO_EMAIL, USERNAME, SETUP_TOKEN));

        verifyNoInteractions(sendGrid);
    }

    @Test
    void sendApprovalEmail_Success() throws Exception {
        // Arrange
        Response sendGridResponse = new Response();
        sendGridResponse.setStatusCode(202); // SendGrid success status code
        when(sendGrid.api(any(Request.class))).thenReturn(sendGridResponse);

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendApprovalEmail(TO_EMAIL, USERNAME, SETUP_TOKEN));

        // Verify
        verify(sendGrid).api(argThat(request -> {
            String body = request.getBody();
            return body.contains(TO_EMAIL) &&
                    body.contains(USERNAME) &&
                    body.contains(SETUP_TOKEN) &&
                    body.contains(FRONTEND_URL) &&
                    body.contains(FROM_EMAIL);
        }));
    }

    @Test
    void sendApprovalEmail_NullEmail_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendApprovalEmail(null, USERNAME, SETUP_TOKEN));

        assertEquals("Email parameters cannot be null", exception.getMessage());
        verifyNoInteractions(sendGrid);
    }

    @Test
    void sendApprovalEmail_NullUsername_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendApprovalEmail(TO_EMAIL, null, SETUP_TOKEN));

        assertEquals("Email parameters cannot be null", exception.getMessage());
        verifyNoInteractions(sendGrid);
    }

    @Test
    void sendApprovalEmail_NullToken_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendApprovalEmail(TO_EMAIL, USERNAME, null));

        assertEquals("Email parameters cannot be null", exception.getMessage());
        verifyNoInteractions(sendGrid);
    }

    @Test
    void sendApprovalEmail_SendGridError_ThrowsException() throws Exception {
        // Arrange
        when(sendGrid.api(any(Request.class))).thenThrow(new RuntimeException("SendGrid API error"));

        // Act & Assert
        EmailSendingException exception = assertThrows(
                EmailSendingException.class,
                () -> emailService.sendApprovalEmail(TO_EMAIL, USERNAME, SETUP_TOKEN));

        assertEquals("Failed to send approval email", exception.getMessage());
        verify(sendGrid).api(any(Request.class));
    }
}