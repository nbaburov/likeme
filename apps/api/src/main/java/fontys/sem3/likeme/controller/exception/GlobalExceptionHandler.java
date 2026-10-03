package fontys.sem3.likeme.controller.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fontys.sem3.likeme.business.exception.analytics.AnalyticsException;
import fontys.sem3.likeme.business.exception.analytics.InvalidAnalyticsRequestException;
import fontys.sem3.likeme.business.exception.invoice.InvalidInvoiceConversionException;
import fontys.sem3.likeme.business.exception.invoice.InvalidInvoiceDataException;
import fontys.sem3.likeme.business.exception.invoice.InvoiceNotFoundException;
import fontys.sem3.likeme.business.exception.invoice.InvoiceServiceException;
import fontys.sem3.likeme.business.exception.notification.InvalidNotificationDataException;
import fontys.sem3.likeme.business.exception.notification.NotificationNotFoundException;
import fontys.sem3.likeme.business.exception.notification.NotificationServiceException;
import fontys.sem3.likeme.business.exception.notification.WebSocketConnectionException;
import fontys.sem3.likeme.business.exception.offer.InvalidOfferConversionException;
import fontys.sem3.likeme.business.exception.offer.InvalidOfferDataException;
import fontys.sem3.likeme.business.exception.offer.OfferNotFoundException;
import fontys.sem3.likeme.business.exception.offer.OfferServiceException;
import fontys.sem3.likeme.business.exception.order.DuplicateOrderException;
import fontys.sem3.likeme.business.exception.order.InvalidOrderConversionException;
import fontys.sem3.likeme.business.exception.order.InvalidOrderDataException;
import fontys.sem3.likeme.business.exception.order.OrderNotFoundException;
import fontys.sem3.likeme.business.exception.order.OrderServiceException;
import fontys.sem3.likeme.business.exception.platform.InvalidPlatformSettingsException;
import fontys.sem3.likeme.business.exception.platform.PlatformSettingsConversionException;
import fontys.sem3.likeme.business.exception.platform.PlatformSettingsNotFoundException;
import fontys.sem3.likeme.business.exception.platform.PlatformSettingsServiceException;
import fontys.sem3.likeme.business.exception.security.auth.AuthenticationException;
import fontys.sem3.likeme.business.exception.security.auth.ExpiredTokenException;
import fontys.sem3.likeme.business.exception.security.auth.InvalidCredentialsException;
import fontys.sem3.likeme.business.exception.security.auth.InvalidRefreshTokenException;
import fontys.sem3.likeme.business.exception.security.auth.InvalidTokenException;
import fontys.sem3.likeme.business.exception.security.auth.RefreshTokenException;
import fontys.sem3.likeme.business.exception.security.auth.UnauthorizedException;
import fontys.sem3.likeme.business.exception.security.auth.UserNotActiveException;
import fontys.sem3.likeme.business.exception.security.jwt.JwtFilterException;
import fontys.sem3.likeme.business.exception.security.jwt.JwtServiceException;
import fontys.sem3.likeme.business.exception.security.password.PasswordServiceException;
import fontys.sem3.likeme.business.exception.security.setuptoken.InvalidSetupPasswordException;
import fontys.sem3.likeme.business.exception.security.setuptoken.InvalidSetupTokenException;
import fontys.sem3.likeme.business.exception.security.setuptoken.SetupTokenServiceException;
import fontys.sem3.likeme.business.exception.user.admin.AdminNotFoundException;
import fontys.sem3.likeme.business.exception.user.admin.AdminServiceException;
import fontys.sem3.likeme.business.exception.user.admin.DuplicateAdminException;
import fontys.sem3.likeme.business.exception.user.admin.InvalidAdminConversionException;
import fontys.sem3.likeme.business.exception.user.admin.InvalidAdminDataException;
import fontys.sem3.likeme.business.exception.user.client.ClientNotFoundException;
import fontys.sem3.likeme.business.exception.user.client.ClientServiceException;
import fontys.sem3.likeme.business.exception.user.client.DuplicateClientException;
import fontys.sem3.likeme.business.exception.user.client.InvalidClientConversionException;
import fontys.sem3.likeme.business.exception.user.client.InvalidClientDataException;
import fontys.sem3.likeme.business.exception.user.influencer.ApplicationPhotosDeletionException;
import fontys.sem3.likeme.business.exception.user.influencer.DuplicateInfluencerDataException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerConversionException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerNotFoundException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerServiceException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerSetupException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerApplicationConversionException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerApplicationException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerDataException;
import fontys.sem3.likeme.business.exception.utils.email.EmailSendingException;
import fontys.sem3.likeme.business.exception.utils.file.FileNotFoundException;
import fontys.sem3.likeme.business.exception.utils.file.FileStorageException;
import fontys.sem3.likeme.controller.dto.error.ErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        // Generic

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "INTERNAL_SERVER_ERROR",
                                "An unexpected error occurred",
                                ex.getMessage());
        }

        // Request Validation

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
                Map<String, String> fieldErrors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = (error instanceof FieldError fieldError) ? fieldError.getField()
                                        : error.getObjectName();
                        String errorMessage = error.getDefaultMessage();

                        // Remove any object prefix (e.g., "billingDetails.zipCode" -> "zipCode")
                        fieldName = fieldName.contains(".") ? fieldName.substring(fieldName.lastIndexOf(".") + 1)
                                        : fieldName;

                        if (fieldErrors.containsKey(fieldName)) {
                                fieldErrors.put(fieldName, fieldErrors.get(fieldName) + " and " + errorMessage);
                        } else {
                                fieldErrors.put(fieldName, errorMessage);
                        }
                });

                // Convert the map to a readable message format
                String detailedMessage = fieldErrors.entrySet().stream()
                                .map(Map.Entry::getValue)
                                .collect(Collectors.joining("\n\n"));

                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "VALIDATION_ERROR",
                                String.format("Validation failed (%d issues)", fieldErrors.size()),
                                detailedMessage);
        }

        // Admin Errors

        @ExceptionHandler(AdminNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleAdminNotFoundException(AdminNotFoundException ex) {
                return createErrorResponse(
                                HttpStatus.NOT_FOUND,
                                "ADMIN_NOT_FOUND",
                                "Admin not found",
                                ex.getMessage());
        }

        @ExceptionHandler(DuplicateAdminException.class)
        public ResponseEntity<ErrorResponseDTO> handleDuplicateAdminException(DuplicateAdminException ex) {
                return createErrorResponse(
                                HttpStatus.CONFLICT,
                                "DUPLICATE_ADMIN_DATA",
                                "Duplicate admin data",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidAdminDataException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidAdminDataException(InvalidAdminDataException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_ADMIN_DATA",
                                "Invalid admin data",
                                ex.getMessage());
        }

        @ExceptionHandler(AdminServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleAdminServiceException(AdminServiceException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "ADMIN_SERVICE_ERROR",
                                "Admin service operation failed",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidAdminConversionException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidAdminConversionException(
                        InvalidAdminConversionException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "ADMIN_CONVERSION_ERROR",
                                "Admin conversion failed",
                                ex.getMessage());
        }

        // Client Errors

        @ExceptionHandler(ClientNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleClientNotFoundException(ClientNotFoundException ex) {
                return createErrorResponse(
                                HttpStatus.NOT_FOUND,
                                "CLIENT_NOT_FOUND",
                                "Client not found",
                                ex.getMessage());
        }

        @ExceptionHandler(DuplicateClientException.class)
        public ResponseEntity<ErrorResponseDTO> handleDuplicateClientException(DuplicateClientException ex) {
                return createErrorResponse(
                                HttpStatus.CONFLICT,
                                "DUPLICATE_CLIENT_DATA",
                                "Duplicate client data",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidClientDataException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidClientDataException(InvalidClientDataException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_CLIENT_DATA",
                                "Invalid client data",
                                ex.getMessage());
        }

        @ExceptionHandler(ClientServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleClientServiceException(ClientServiceException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "CLIENT_SERVICE_ERROR",
                                "Client service operation failed",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidClientConversionException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidClientConversionException(
                        InvalidClientConversionException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "CLIENT_CONVERSION_ERROR",
                                "Client conversion failed",
                                ex.getMessage());
        }

        // Influencer Errors

        @ExceptionHandler(InfluencerNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleInfluencerNotFoundException(InfluencerNotFoundException ex) {
                return createErrorResponse(
                                HttpStatus.NOT_FOUND,
                                "INFLUENCER_NOT_FOUND",
                                "Influencer not found",
                                ex.getMessage());
        }

        @ExceptionHandler(DuplicateInfluencerDataException.class)
        public ResponseEntity<ErrorResponseDTO> handleDuplicateInfluencerDataException(
                        DuplicateInfluencerDataException ex) {
                return createErrorResponse(
                                HttpStatus.CONFLICT,
                                "DUPLICATE_INFLUENCER_DATA",
                                "Duplicate influencer data",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidInfluencerDataException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidInfluencerDataException(
                        InvalidInfluencerDataException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_INFLUENCER_DATA",
                                "Invalid influencer data",
                                ex.getMessage());
        }

        @ExceptionHandler(InfluencerServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleInfluencerServiceException(InfluencerServiceException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "INFLUENCER_SERVICE_ERROR",
                                "Influencer service operation failed",
                                ex.getMessage());
        }

        @ExceptionHandler(InfluencerSetupException.class)
        public ResponseEntity<ErrorResponseDTO> handleInfluencerSetupException(InfluencerSetupException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INFLUENCER_SETUP_ERROR",
                                "Influencer setup failed",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidInfluencerApplicationException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidInfluencerApplicationException(
                        InvalidInfluencerApplicationException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_INFLUENCER_APPLICATION",
                                "Invalid influencer application",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidInfluencerApplicationConversionException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidInfluencerApplicationConversionException(
                        InvalidInfluencerApplicationConversionException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "INFLUENCER_APPLICATION_CONVERSION_ERROR",
                                "Influencer application conversion failed",
                                ex.getMessage());
        }

        @ExceptionHandler(InfluencerConversionException.class)
        public ResponseEntity<ErrorResponseDTO> handleInfluencerConversionException(InfluencerConversionException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "INFLUENCER_CONVERSION_ERROR",
                                "Influencer conversion failed",
                                ex.getMessage());
        }

        @ExceptionHandler(ApplicationPhotosDeletionException.class)
        public ResponseEntity<ErrorResponseDTO> handleApplicationPhotosDeletionException(
                        ApplicationPhotosDeletionException ex) {
                return createErrorResponse(
                                HttpStatus.PARTIAL_CONTENT,
                                "PHOTOS_DELETION_INCOMPLETE",
                                "Some photos could not be deleted",
                                ex.getMessage());
        }

        // Platform Settings Errors

        @ExceptionHandler(InvalidPlatformSettingsException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidPlatformSettingsException(
                        InvalidPlatformSettingsException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_PLATFORM_SETTINGS",
                                "Invalid platform settings",
                                ex.getMessage());
        }

        @ExceptionHandler(PlatformSettingsConversionException.class)
        public ResponseEntity<ErrorResponseDTO> handlePlatformSettingsConversionException(
                        PlatformSettingsConversionException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "PLATFORM_SETTINGS_CONVERSION_ERROR",
                                "Platform settings conversion failed",
                                ex.getMessage());
        }

        @ExceptionHandler(PlatformSettingsNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handlePlatformSettingsNotFoundException(
                        PlatformSettingsNotFoundException ex) {
                return createErrorResponse(
                                HttpStatus.NOT_FOUND,
                                "PLATFORM_SETTINGS_NOT_FOUND",
                                "Platform settings not found",
                                ex.getMessage());
        }

        @ExceptionHandler(PlatformSettingsServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handlePlatformSettingsServiceException(
                        PlatformSettingsServiceException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "PLATFORM_SETTINGS_SERVICE_ERROR",
                                "Platform settings service operation failed",
                                ex.getMessage());
        }

        // Security Errors

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidCredentialsException(InvalidCredentialsException ex) {
                return createErrorResponse(
                                HttpStatus.UNAUTHORIZED,
                                "INVALID_CREDENTIALS",
                                "Authentication failed",
                                ex.getMessage());
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(UnauthorizedException ex) {
                return createErrorResponse(
                                HttpStatus.UNAUTHORIZED,
                                "UNAUTHORIZED",
                                "Access denied",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidTokenException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidTokenException(InvalidTokenException ex) {
                return createErrorResponse(
                                HttpStatus.UNAUTHORIZED,
                                "INVALID_TOKEN",
                                "Invalid authentication token",
                                ex.getMessage());
        }

        @ExceptionHandler(JwtFilterException.class)
        public ResponseEntity<ErrorResponseDTO> handleJwtFilterException(JwtFilterException ex) {
                return createErrorResponse(
                                HttpStatus.UNAUTHORIZED,
                                "JWT_FILTER_ERROR",
                                "JWT authentication failed",
                                ex.getMessage());
        }

        @ExceptionHandler(JwtServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleJwtServiceException(JwtServiceException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "JWT_SERVICE_ERROR",
                                "JWT service operation failed",
                                ex.getMessage());
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
                return createErrorResponse(
                                HttpStatus.FORBIDDEN,
                                "ACCESS_DENIED",
                                "Access denied",
                                ex.getMessage());
        }

        // Password Service Errors

        @ExceptionHandler(PasswordServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handlePasswordServiceException(PasswordServiceException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "PASSWORD_SERVICE_ERROR",
                                "Password service operation failed",
                                ex.getMessage());
        }

        // Setup Token Errors

        @ExceptionHandler(InvalidSetupPasswordException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidSetupPasswordException(InvalidSetupPasswordException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_SETUP_PASSWORD",
                                "Invalid setup password",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidSetupTokenException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidSetupTokenException(InvalidSetupTokenException ex) {
                return createErrorResponse(
                                HttpStatus.UNAUTHORIZED,
                                "INVALID_SETUP_TOKEN",
                                "Invalid setup token",
                                ex.getMessage());
        }

        @ExceptionHandler(SetupTokenServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleSetupTokenServiceException(SetupTokenServiceException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "SETUP_TOKEN_SERVICE_ERROR",
                                "Setup token service operation failed",
                                ex.getMessage());
        }

        // Email Sending Errors

        @ExceptionHandler(EmailSendingException.class)
        public ResponseEntity<ErrorResponseDTO> handleEmailSendingException(EmailSendingException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "EMAIL_SENDING_ERROR",
                                "Failed to send email",
                                ex.getMessage());
        }

        // File Storage Errors

        @ExceptionHandler(FileNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleFileNotFoundException(FileNotFoundException ex) {
                return createErrorResponse(
                                HttpStatus.NOT_FOUND,
                                "FILE_NOT_FOUND",
                                "File not found",
                                ex.getMessage());
        }

        @ExceptionHandler(FileStorageException.class)
        public ResponseEntity<ErrorResponseDTO> handleFileStorageException(FileStorageException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "FILE_STORAGE_ERROR",
                                "File storage operation failed",
                                ex.getMessage());
        }

        // User Not Active Exception

        @ExceptionHandler(UserNotActiveException.class)
        public ResponseEntity<ErrorResponseDTO> handleUserNotActiveException(UserNotActiveException ex) {
                return createErrorResponse(
                                HttpStatus.FORBIDDEN,
                                "USER_NOT_ACTIVE",
                                "User account is not active",
                                ex.getMessage());
        }

        @ExceptionHandler(RefreshTokenException.class)
        public ResponseEntity<ErrorResponseDTO> handleRefreshTokenException(RefreshTokenException ex) {
                return createErrorResponse(
                                HttpStatus.UNAUTHORIZED,
                                "REFRESH_TOKEN_ERROR",
                                "Refresh token error",
                                ex.getMessage());
        }

        @ExceptionHandler(ExpiredTokenException.class)
        public ResponseEntity<ErrorResponseDTO> handleExpiredTokenException(ExpiredTokenException ex) {
                return createErrorResponse(
                                HttpStatus.UNAUTHORIZED,
                                "TOKEN_EXPIRED",
                                "Token has expired",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidRefreshTokenException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
                return createErrorResponse(
                                HttpStatus.UNAUTHORIZED,
                                "INVALID_REFRESH_TOKEN",
                                "Invalid refresh token",
                                ex.getMessage());
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "AUTHENTICATION_ERROR",
                                "Authentication process failed",
                                ex.getMessage());
        }

        // Invoice Errors
        @ExceptionHandler(InvoiceNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvoiceNotFoundException(InvoiceNotFoundException ex) {
                return createErrorResponse(
                                HttpStatus.NOT_FOUND,
                                "INVOICE_NOT_FOUND",
                                "Invoice not found",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidInvoiceDataException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidInvoiceDataException(InvalidInvoiceDataException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_INVOICE_DATA",
                                "Invalid invoice data",
                                ex.getMessage());
        }

        @ExceptionHandler(InvoiceServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvoiceServiceException(InvoiceServiceException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "INVOICE_SERVICE_ERROR",
                                "Invoice service operation failed",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidInvoiceConversionException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidInvoiceConversionException(
                        InvalidInvoiceConversionException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "INVOICE_CONVERSION_ERROR",
                                "Invoice conversion failed",
                                ex.getMessage());
        }

        // Offer Errors
        @ExceptionHandler(OfferNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleOfferNotFoundException(OfferNotFoundException ex) {
                return createErrorResponse(
                                HttpStatus.NOT_FOUND,
                                "OFFER_NOT_FOUND",
                                "Offer not found",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidOfferDataException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidOfferDataException(InvalidOfferDataException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_OFFER_DATA",
                                "Invalid offer data",
                                ex.getMessage());
        }

        @ExceptionHandler(OfferServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleOfferServiceException(OfferServiceException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "OFFER_SERVICE_ERROR",
                                "Offer service operation failed",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidOfferConversionException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidOfferConversionException(
                        InvalidOfferConversionException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "OFFER_CONVERSION_ERROR",
                                "Offer conversion failed",
                                ex.getMessage());
        }

        // Order Errors
        @ExceptionHandler(OrderNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleOrderNotFoundException(OrderNotFoundException ex) {
                return createErrorResponse(
                                HttpStatus.NOT_FOUND,
                                "ORDER_NOT_FOUND",
                                "Order not found",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidOrderDataException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidOrderDataException(InvalidOrderDataException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_ORDER_DATA",
                                "Invalid order data",
                                ex.getMessage());
        }

        @ExceptionHandler(OrderServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleOrderServiceException(OrderServiceException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "ORDER_SERVICE_ERROR",
                                "Order service operation failed",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidOrderConversionException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidOrderConversionException(
                        InvalidOrderConversionException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "ORDER_CONVERSION_ERROR",
                                "Order conversion failed",
                                ex.getMessage());
        }

        @ExceptionHandler(DuplicateOrderException.class)
        public ResponseEntity<ErrorResponseDTO> handleDuplicateOrderException(DuplicateOrderException ex) {
                return createErrorResponse(
                                HttpStatus.CONFLICT,
                                "DUPLICATE_ORDER",
                                "Duplicate order detected",
                                ex.getMessage());
        }

        // Notification Errors
        @ExceptionHandler(NotificationNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleNotificationNotFoundException(NotificationNotFoundException ex) {
                return createErrorResponse(
                                HttpStatus.NOT_FOUND,
                                "NOTIFICATION_NOT_FOUND",
                                "Notification not found",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidNotificationDataException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidNotificationDataException(
                        InvalidNotificationDataException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_NOTIFICATION_DATA",
                                "Invalid notification data",
                                ex.getMessage());
        }

        @ExceptionHandler(NotificationServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleNotificationServiceException(NotificationServiceException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "NOTIFICATION_SERVICE_ERROR",
                                "Notification service operation failed",
                                ex.getMessage());
        }

        @ExceptionHandler(WebSocketConnectionException.class)
        public ResponseEntity<ErrorResponseDTO> handleWebSocketConnectionException(WebSocketConnectionException ex) {
                return createErrorResponse(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "WEBSOCKET_CONNECTION_ERROR",
                                "WebSocket connection failed",
                                ex.getMessage());
        }

        // Analytics Errors
        @ExceptionHandler(InvalidAnalyticsRequestException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponseDTO> handleInvalidAnalyticsRequest(InvalidAnalyticsRequestException ex) {
                return createErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_ANALYTICS_REQUEST",
                                "Invalid analytics request",
                                ex.getMessage());
        }

        @ExceptionHandler(AnalyticsException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ErrorResponseDTO> handleAnalyticsException(AnalyticsException ex) {
                return createErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "ANALYTICS_ERROR",
                                "Analytics operation failed",
                                ex.getMessage());
        }

        // Helper
        private ResponseEntity<ErrorResponseDTO> createErrorResponse(
                        HttpStatus status,
                        String code,
                        String title,
                        String message) {
                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                                code,
                                title,
                                message,
                                LocalDateTime.now().format(formatter));

                return ResponseEntity
                                .status(status)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(errorResponse);
        }
}