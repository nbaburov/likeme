package fontys.sem3.likeme.business.impl.invoice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import fontys.sem3.likeme.domain.invoice.InvoiceStatus;
import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.offer.OfferType;
import fontys.sem3.likeme.domain.order.OrderDetails;
import fontys.sem3.likeme.domain.order.OrderStatus;
import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.domain.user.influencer.Influencer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.invoice.InvalidInvoiceDataException;
import fontys.sem3.likeme.business.exception.invoice.InvoiceNotFoundException;
import fontys.sem3.likeme.business.exception.invoice.InvoiceServiceException;
import fontys.sem3.likeme.business.validator.invoice.InvoiceValidator;
import fontys.sem3.likeme.domain.invoice.Invoice;
import fontys.sem3.likeme.domain.order.Order;
import fontys.sem3.likeme.repository.interfaces.invoice.InvoiceRepository;
import fontys.sem3.likeme.repository.interfaces.order.OrderRepository;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {
        @Mock
        private InvoiceRepository invoiceRepository;

        @Mock
        private InvoiceValidator invoiceValidator;

        @Mock
        private OrderRepository orderRepository;

        @InjectMocks
        private InvoiceServiceImpl invoiceService;

        private Invoice testInvoice;
        private Order testOrder;
        private Offer testOffer;
        private Client testClient;
        private Influencer testInfluencer;
        private OrderDetails testOrderDetails;

        private static final Long INVOICE_ID = 1L;
        private static final Long ORDER_ID = 1L;
        private static final Long CLIENT_ID = 1L;
        private static final Long INFLUENCER_ID = 1L;
        private static final BigDecimal AMOUNT = new BigDecimal("100.00");
        private static final Date CREATED_ON = new Date();
        private static final Date UPDATED_ON = new Date();

        @BeforeEach
        void setUp() {
                testOffer = Offer.builder()
                                .id(1L)
                                .title("Test Offer")
                                .price(100.0)
                                .type(OfferType.LIKE)
                                .build();

                testClient = Client.builder()
                                .id(CLIENT_ID)
                                .build();

                testInfluencer = Influencer.builder()
                                .id(INFLUENCER_ID)
                                .build();

                testOrderDetails = OrderDetails.builder()
                                .postId("123")
                                .build();

                testOrder = Order.builder()
                                .id(ORDER_ID)
                                .offer(testOffer)
                                .orderedBy(testClient)
                                .updatedBy(testInfluencer)
                                .details(testOrderDetails)
                                .status(OrderStatus.PENDING)
                                .createdOn(CREATED_ON)
                                .updatedOn(UPDATED_ON)
                                .build();

                testInvoice = Invoice.builder()
                                .id(INVOICE_ID)
                                .amount(AMOUNT)
                                .status(InvoiceStatus.PENDING)
                                .createdOn(CREATED_ON)
                                .updatedOn(UPDATED_ON)
                                .build();

                testOrder.setInvoice(testInvoice);
        }

        @Test
        void createInvoice_Success() {
                Invoice invoiceToCreate = Invoice.builder()
                                .amount(AMOUNT)
                                .status(InvoiceStatus.PENDING)
                                .createdOn(CREATED_ON)
                                .updatedOn(UPDATED_ON)
                                .build();

                when(invoiceRepository.save(invoiceToCreate)).thenReturn(testInvoice);

                Invoice actualInvoice = invoiceService.createInvoice(invoiceToCreate);

                assertEquals(testInvoice.getId(), actualInvoice.getId());
                assertEquals(testInvoice.getAmount(), actualInvoice.getAmount());
                assertEquals(testInvoice.getStatus(), actualInvoice.getStatus());
                assertEquals(testInvoice.getCreatedOn(), actualInvoice.getCreatedOn());
                assertEquals(testInvoice.getUpdatedOn(), actualInvoice.getUpdatedOn());

                verify(invoiceValidator).validateCreate(invoiceToCreate);
                verify(invoiceRepository).save(invoiceToCreate);
        }

        @Test
        void createInvoice_WhenInvoiceIsNull_ThrowsInvalidInvoiceDataException() {
                InvalidInvoiceDataException exception = assertThrows(
                                InvalidInvoiceDataException.class,
                                () -> invoiceService.createInvoice(null));

                assertEquals("Invoice cannot be null", exception.getMessage());
                verify(invoiceValidator, never()).validateCreate(null);
                verify(invoiceRepository, never()).save(null);
        }

        @Test
        void getInvoiceByOrderId_Success() {
                when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));

                Invoice actualInvoice = invoiceService.getInvoiceByOrderId(ORDER_ID);

                assertEquals(testInvoice.getId(), actualInvoice.getId());
                assertEquals(testInvoice.getAmount(), actualInvoice.getAmount());
                assertEquals(testInvoice.getStatus(), actualInvoice.getStatus());
                assertEquals(testInvoice.getCreatedOn(), actualInvoice.getCreatedOn());
                assertEquals(testInvoice.getUpdatedOn(), actualInvoice.getUpdatedOn());

                verify(orderRepository).findById(ORDER_ID);
        }

        @Test
        void getInvoiceByOrderId_WhenOrderNotFound_ThrowsInvoiceNotFoundException() {
                when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

                InvoiceNotFoundException exception = assertThrows(
                                InvoiceNotFoundException.class,
                                () -> invoiceService.getInvoiceByOrderId(ORDER_ID));

                assertEquals("Order not found for id: " + ORDER_ID, exception.getMessage());
                verify(orderRepository).findById(ORDER_ID);
        }

        @Test
        void getInvoicesByClientId_Success() {
                List<Order> clientOrders = List.of(testOrder);
                when(orderRepository.findByOrderedClientId(CLIENT_ID)).thenReturn(clientOrders);

                List<Invoice> actualInvoices = invoiceService.getInvoicesByClientId(CLIENT_ID);

                assertEquals(1, actualInvoices.size());
                assertEquals(testInvoice, actualInvoices.get(0));
                verify(orderRepository).findByOrderedClientId(CLIENT_ID);
        }

        @Test
        void getInvoicesByInfluencerId_Success() {
                List<Order> influencerOrders = List.of(testOrder);
                when(orderRepository.findByInfluencerId(INFLUENCER_ID)).thenReturn(influencerOrders);

                List<Invoice> actualInvoices = invoiceService.getInvoicesByInfluencerId(INFLUENCER_ID);

                assertEquals(1, actualInvoices.size());
                assertEquals(testInvoice, actualInvoices.get(0));
                verify(orderRepository).findByInfluencerId(INFLUENCER_ID);
        }

        @Test
        void updateInvoice_Success() {
                Invoice invoiceToUpdate = Invoice.builder()
                                .id(INVOICE_ID)
                                .amount(new BigDecimal("150.00"))
                                .status(InvoiceStatus.PAID)
                                .createdOn(CREATED_ON)
                                .updatedOn(new Date())
                                .build();

                when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(testInvoice));
                when(invoiceRepository.save(invoiceToUpdate)).thenReturn(invoiceToUpdate);

                Invoice actualInvoice = invoiceService.updateInvoice(invoiceToUpdate);

                assertEquals(invoiceToUpdate.getId(), actualInvoice.getId());
                assertEquals(invoiceToUpdate.getAmount(), actualInvoice.getAmount());
                assertEquals(invoiceToUpdate.getStatus(), actualInvoice.getStatus());
                assertEquals(invoiceToUpdate.getCreatedOn(), actualInvoice.getCreatedOn());
                assertEquals(invoiceToUpdate.getUpdatedOn(), actualInvoice.getUpdatedOn());

                verify(invoiceRepository).findById(INVOICE_ID);
                verify(invoiceRepository).save(invoiceToUpdate);
        }

        @Test
        void updateInvoice_WhenInvoiceNotFound_ThrowsInvoiceNotFoundException() {
                Invoice invoiceToUpdate = Invoice.builder()
                                .id(INVOICE_ID)
                                .amount(AMOUNT)
                                .build();

                when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.empty());

                InvoiceNotFoundException exception = assertThrows(
                                InvoiceNotFoundException.class,
                                () -> invoiceService.updateInvoice(invoiceToUpdate));

                assertEquals("Invoice not found with id: " + INVOICE_ID, exception.getMessage());
                verify(invoiceRepository).findById(INVOICE_ID);
                verify(invoiceRepository, never()).save(invoiceToUpdate);
        }

        @Test
        void deleteInvoice_Success() {
                when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(testInvoice));

                invoiceService.deleteInvoice(INVOICE_ID);

                verify(invoiceRepository).findById(INVOICE_ID);
                verify(invoiceRepository).deleteById(INVOICE_ID);
        }

        @Test
        void deleteInvoice_WhenInvoiceNotFound_ThrowsInvoiceNotFoundException() {
                when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.empty());

                InvoiceNotFoundException exception = assertThrows(
                                InvoiceNotFoundException.class,
                                () -> invoiceService.deleteInvoice(INVOICE_ID));

                assertEquals("Invoice not found with id: " + INVOICE_ID, exception.getMessage());
                verify(invoiceRepository).findById(INVOICE_ID);
                verify(invoiceRepository, never()).deleteById(INVOICE_ID);
        }

        @Test
        void createInvoice_DatabaseError_ThrowsInvoiceServiceException() {
                Invoice invoiceToCreate = Invoice.builder()
                                .amount(AMOUNT)
                                .status(InvoiceStatus.PENDING)
                                .build();

                when(invoiceRepository.save(any(Invoice.class)))
                                .thenThrow(new RuntimeException("Database error"));

                InvoiceServiceException exception = assertThrows(
                                InvoiceServiceException.class,
                                () -> invoiceService.createInvoice(invoiceToCreate));

                assertEquals("Failed to create invoice", exception.getMessage());
                verify(invoiceValidator).validateCreate(invoiceToCreate);
                verify(invoiceRepository).save(invoiceToCreate);
        }

        @Test
        void updateInvoice_NullId_ThrowsInvalidInvoiceDataException() {
                Invoice invoiceToUpdate = Invoice.builder()
                                .amount(AMOUNT)
                                .status(InvoiceStatus.PENDING)
                                .build();

                InvalidInvoiceDataException exception = assertThrows(
                                InvalidInvoiceDataException.class,
                                () -> invoiceService.updateInvoice(invoiceToUpdate));

                assertEquals("Invoice and ID cannot be null", exception.getMessage());
                verifyNoInteractions(invoiceRepository);
        }

        @Test
        void updateInvoice_DatabaseError_ThrowsInvoiceServiceException() {
                Invoice invoiceToUpdate = Invoice.builder()
                                .id(INVOICE_ID)
                                .amount(AMOUNT)
                                .status(InvoiceStatus.PENDING)
                                .build();

                when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(testInvoice));
                when(invoiceRepository.save(any(Invoice.class)))
                                .thenThrow(new RuntimeException("Database error"));

                InvoiceServiceException exception = assertThrows(
                                InvoiceServiceException.class,
                                () -> invoiceService.updateInvoice(invoiceToUpdate));

                assertEquals("Failed to update invoice with id: " + INVOICE_ID, exception.getMessage());
                verify(invoiceRepository).findById(INVOICE_ID);
                verify(invoiceRepository).save(invoiceToUpdate);
        }

        @Test
        void deleteInvoice_NullId_ThrowsInvalidInvoiceDataException() {
                InvoiceServiceException exception = assertThrows(
                        InvoiceServiceException.class,
                                () -> invoiceService.deleteInvoice(null));

                assertEquals("Failed to delete invoice: null", exception.getMessage());
                verifyNoInteractions(invoiceRepository);
        }

        @Test
        void deleteInvoice_DatabaseError_ThrowsInvoiceServiceException() {
                when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(testInvoice));
                doThrow(new RuntimeException("Database error"))
                                .when(invoiceRepository).deleteById(INVOICE_ID);

                InvoiceServiceException exception = assertThrows(
                                InvoiceServiceException.class,
                                () -> invoiceService.deleteInvoice(INVOICE_ID));

                assertEquals("Failed to delete invoice: " + INVOICE_ID, exception.getMessage());
                verify(invoiceRepository).findById(INVOICE_ID);
                verify(invoiceRepository).deleteById(INVOICE_ID);
        }

        @Test
        void getInvoiceByOrderId_NullId_ThrowsInvalidInvoiceDataException() {
                InvalidInvoiceDataException exception = assertThrows(
                                InvalidInvoiceDataException.class,
                                () -> invoiceService.getInvoiceByOrderId(null));

                assertEquals("Order ID cannot be null", exception.getMessage());
                verifyNoInteractions(orderRepository);
        }

        @Test
        void getInvoiceByOrderId_DatabaseError_ThrowsInvoiceServiceException() {
                when(orderRepository.findById(ORDER_ID))
                                .thenThrow(new RuntimeException("Database error"));

                InvoiceServiceException exception = assertThrows(
                                InvoiceServiceException.class,
                                () -> invoiceService.getInvoiceByOrderId(ORDER_ID));

                assertEquals("Error retrieving invoice for order: " + ORDER_ID, exception.getMessage());
                verify(orderRepository).findById(ORDER_ID);
        }

        @Test
        void getInvoiceByOrderId_NoInvoiceForOrder_ThrowsInvoiceNotFoundException() {
                testOrder.setInvoice(null);
                when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));

                InvoiceNotFoundException exception = assertThrows(
                                InvoiceNotFoundException.class,
                                () -> invoiceService.getInvoiceByOrderId(ORDER_ID));

                assertEquals("Invoice not found for order: " + ORDER_ID, exception.getMessage());
                verify(orderRepository).findById(ORDER_ID);
        }
}