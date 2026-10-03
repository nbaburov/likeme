package fontys.sem3.likeme.business.impl.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.order.InvalidOrderDataException;
import fontys.sem3.likeme.business.exception.order.OrderNotFoundException;
import fontys.sem3.likeme.business.exception.order.OrderServiceException;
import fontys.sem3.likeme.business.interfaces.invoice.InvoiceService;
import fontys.sem3.likeme.business.interfaces.offer.OfferService;
import fontys.sem3.likeme.business.validator.order.OrderValidator;
import fontys.sem3.likeme.domain.invoice.Invoice;
import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.offer.OfferType;
import fontys.sem3.likeme.domain.order.Order;
import fontys.sem3.likeme.domain.order.OrderDetails;
import fontys.sem3.likeme.domain.order.OrderStatus;
import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.repository.interfaces.order.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderValidator orderValidator;
    @Mock
    private OfferService offerService;
    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private Offer testOffer;
    private Client testClient;
    private Influencer testInfluencer;
    private Invoice testInvoice;
    private OrderDetails testOrderDetails;

    private static final Long ORDER_ID = 1L;
    private static final Long OFFER_ID = 1L;
    private static final Long CLIENT_ID = 1L;
    private static final Long INFLUENCER_ID = 1L;
    private static final Long INVOICE_ID = 1L;
    private static final Double PRICE = 100.0;
    private static final Date CREATED_ON = new Date();
    private static final Date UPDATED_ON = new Date();

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
                .id(CLIENT_ID)
                .build();

        testInfluencer = Influencer.builder()
                .id(INFLUENCER_ID)
                .build();

        testOffer = Offer.builder()
                .id(OFFER_ID)
                .price(PRICE)
                .type(OfferType.LIKE)
                .build();

        testInvoice = Invoice.builder()
                .id(INVOICE_ID)
                .amount(BigDecimal.valueOf(PRICE))
                .build();

        testOrderDetails = OrderDetails.builder()
                .postId("test-post-id")
                .build();

        testOrder = Order.builder()
                .id(ORDER_ID)
                .offer(testOffer)
                .orderedBy(testClient)
                .invoice(testInvoice)
                .details(testOrderDetails)
                .status(OrderStatus.PENDING)
                .createdOn(CREATED_ON)
                .id(ORDER_ID)
                .updatedOn(UPDATED_ON)
                .build();
    }

    @Test
    void createOrder_Success() {
        Order orderToCreate = Order.builder()
                .id(ORDER_ID)
                .offer(testOffer)
                .orderedBy(testClient)
                .details(testOrderDetails)
                .status(OrderStatus.PENDING)
                .createdOn(CREATED_ON)
                .build();

        Invoice expectedInvoice = Invoice.builder()
                .amount(BigDecimal.valueOf(testOffer.getPrice()))
                .build();

        when(invoiceService.createInvoice(
                argThat(invoice -> invoice.getAmount().compareTo(BigDecimal.valueOf(testOffer.getPrice())) == 0)))
                .thenReturn(testInvoice);
        when(orderRepository.save(argThat(order -> order.getOffer().equals(testOffer) &&
                order.getOrderedBy().equals(testClient) &&
                order.getInvoice().equals(testInvoice))))
                .thenReturn(orderToCreate);

        Order actualOrder = orderService.createOrder(orderToCreate);

        assertNotNull(actualOrder);
        assertEquals(ORDER_ID, actualOrder.getId());
        assertEquals(testOffer, actualOrder.getOffer());
        assertEquals(testClient, actualOrder.getOrderedBy());
        assertEquals(testInvoice, actualOrder.getInvoice());
        assertEquals(testOrderDetails, actualOrder.getDetails());
        assertEquals(OrderStatus.PENDING, actualOrder.getStatus());

        verify(orderValidator).validateCreate(orderToCreate);
        verify(invoiceService).createInvoice(
                argThat(invoice -> invoice.getAmount().compareTo(BigDecimal.valueOf(testOffer.getPrice())) == 0));
        verify(orderRepository).save(argThat(order -> order.getOffer().equals(testOffer) &&
                order.getOrderedBy().equals(testClient) &&
                order.getInvoice().equals(testInvoice)));
    }

    @Test
    void createOrder_WhenOrderIsNull_ThrowsInvalidOrderDataException() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderService.createOrder(null));

        assertEquals("Order cannot be null", exception.getMessage());
        verify(orderValidator, never()).validateCreate(any());
        verify(invoiceService, never()).createInvoice(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrder_Success() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));

        Order actualOrder = orderService.getOrder(ORDER_ID);

        assertNotNull(actualOrder);
        assertEquals(testOrder.getId(), actualOrder.getId());
        assertEquals(testOrder.getOffer(), actualOrder.getOffer());
        assertEquals(testOrder.getOrderedBy(), actualOrder.getOrderedBy());
        assertEquals(testOrder.getInvoice(), actualOrder.getInvoice());

        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void getOrder_WhenNotFound_ThrowsOrderNotFoundException() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrder(ORDER_ID));

        assertEquals("Order not found with id: " + ORDER_ID, exception.getMessage());
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void completeOrder_Success() {
        Order orderToUpdate = Order.builder()
                .id(ORDER_ID)
                .offer(testOffer)
                .orderedBy(testClient)
                .invoice(testInvoice)
                .details(testOrderDetails)
                .status(OrderStatus.COMPLETE)
                .updatedBy(testInfluencer)
                .createdOn(CREATED_ON)
                .updatedOn(UPDATED_ON)
                .build();

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(orderToUpdate)).thenReturn(orderToUpdate);

        Order completedOrder = orderService.completeOrder(ORDER_ID, testInfluencer);

        assertNotNull(completedOrder);
        assertEquals(OrderStatus.COMPLETE, completedOrder.getStatus());
        assertEquals(testInfluencer, completedOrder.getUpdatedBy());
        assertNotNull(completedOrder.getUpdatedOn());

        verify(orderValidator).validateCompletion(testOrder, testInfluencer);
        verify(orderRepository).save(orderToUpdate);
    }

    @Test
    void getOrdersByOffer_Success() {
        List<Order> expectedOrders = List.of(testOrder);
        when(offerService.getOffer(OFFER_ID)).thenReturn(testOffer);
        when(orderRepository.findByOfferId(OFFER_ID)).thenReturn(expectedOrders);

        List<Order> actualOrders = orderService.getOrdersByOffer(OFFER_ID);

        assertEquals(expectedOrders.size(), actualOrders.size());
        assertEquals(expectedOrders.get(0), actualOrders.get(0));

        verify(offerService).getOffer(OFFER_ID);
        verify(orderRepository).findByOfferId(OFFER_ID);
    }

    @Test
    void getOrderByInvoice_Success() {
        when(orderRepository.findByInvoiceId(INVOICE_ID)).thenReturn(Optional.of(testOrder));

        Order actualOrder = orderService.getOrderByInvoice(INVOICE_ID);

        assertNotNull(actualOrder);
        assertEquals(testOrder.getId(), actualOrder.getId());
        assertEquals(testOrder.getInvoice().getId(), actualOrder.getInvoice().getId());

        verify(orderRepository).findByInvoiceId(INVOICE_ID);
    }

    @Test
    void getOrdersByClient_Success() {
        List<Order> expectedOrders = List.of(testOrder);
        when(orderRepository.findByOrderedClientId(CLIENT_ID)).thenReturn(expectedOrders);

        List<Order> actualOrders = orderService.getOrdersByClient(CLIENT_ID);

        assertEquals(expectedOrders.size(), actualOrders.size());
        assertEquals(expectedOrders.get(0), actualOrders.get(0));

        verify(orderRepository).findByOrderedClientId(CLIENT_ID);
    }

    @Test
    void getOrdersByInfluencer_Success() {
        List<Order> expectedOrders = List.of(testOrder);
        when(orderRepository.findByInfluencerId(INFLUENCER_ID)).thenReturn(expectedOrders);

        List<Order> actualOrders = orderService.getOrdersByInfluencer(INFLUENCER_ID);

        assertEquals(expectedOrders.size(), actualOrders.size());
        assertEquals(expectedOrders.get(0), actualOrders.get(0));

        verify(orderRepository).findByInfluencerId(INFLUENCER_ID);
    }

    @Test
    void getAllOrders_Success() {
        List<Order> expectedOrders = List.of(testOrder);
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        List<Order> actualOrders = orderService.getAllOrders();

        assertEquals(expectedOrders.size(), actualOrders.size());
        assertEquals(expectedOrders.get(0), actualOrders.get(0));

        verify(orderRepository).findAll();
    }

    @Test
    void deleteOrder_Success() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));

        orderService.deleteOrder(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository).deleteById(ORDER_ID);
    }

    @Test
    void deleteOrder_WhenNotFound_ThrowsOrderNotFoundException() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.deleteOrder(ORDER_ID));

        assertEquals("Order not found with id: " + ORDER_ID, exception.getMessage());
        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).deleteById(ORDER_ID);
    }

    @Test
    void createOrder_ValidationError_ThrowsInvalidOrderDataException() {
        Order orderToCreate = Order.builder()
                .offer(testOffer)
                .orderedBy(testClient)
                .details(testOrderDetails)
                .status(OrderStatus.PENDING)
                .build();

        Invoice expectedInvoice = Invoice.builder()
                .amount(BigDecimal.valueOf(testOffer.getPrice()))
                .build();

        when(invoiceService.createInvoice(
                argThat(invoice -> invoice.getAmount().equals(BigDecimal.valueOf(testOffer.getPrice())))))
                .thenReturn(testInvoice);

        doThrow(new InvalidOrderDataException("Invalid order data"))
                .when(orderValidator)
                .validateCreate(argThat(order -> order.getOffer().equals(testOffer) &&
                        order.getOrderedBy().equals(testClient) &&
                        order.getDetails().equals(testOrderDetails) &&
                        order.getStatus().equals(OrderStatus.PENDING) &&
                        order.getInvoice().equals(testInvoice)));

        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderService.createOrder(orderToCreate));

        assertEquals("Invalid order data", exception.getMessage());
        verify(invoiceService).createInvoice(
                argThat(invoice -> invoice.getAmount().equals(BigDecimal.valueOf(testOffer.getPrice()))));
        verify(orderValidator).validateCreate(argThat(order -> order.getOffer().equals(testOffer) &&
                order.getOrderedBy().equals(testClient) &&
                order.getDetails().equals(testOrderDetails) &&
                order.getStatus().equals(OrderStatus.PENDING) &&
                order.getInvoice().equals(testInvoice)));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void createOrder_ServiceError_ThrowsOrderServiceException() {
        Order orderToCreate = Order.builder()
                .offer(testOffer)
                .orderedBy(testClient)
                .details(testOrderDetails)
                .status(OrderStatus.PENDING)
                .build();

        when(invoiceService.createInvoice(any())).thenThrow(new RuntimeException("Database error"));

        OrderServiceException exception = assertThrows(
                OrderServiceException.class,
                () -> orderService.createOrder(orderToCreate));

        assertEquals("Failed to create order", exception.getMessage());
        verify(invoiceService).createInvoice(any());
        verifyNoInteractions(orderRepository);
    }

    @Test
    void getOrder_NullId_ThrowsInvalidOrderDataException() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderService.getOrder(null));

        assertEquals("Order ID cannot be null", exception.getMessage());
        verifyNoInteractions(orderRepository);
    }

    @Test
    void getOrder_ServiceError_ThrowsOrderServiceException() {
        when(orderRepository.findById(ORDER_ID))
                .thenThrow(new RuntimeException("Database error"));

        OrderServiceException exception = assertThrows(
                OrderServiceException.class,
                () -> orderService.getOrder(ORDER_ID));

        assertEquals("Error retrieving order with id: " + ORDER_ID, exception.getMessage());
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void getOrderByInvoice_NullId_ThrowsInvalidOrderDataException() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderService.getOrderByInvoice(null));

        assertEquals("Invoice ID cannot be null", exception.getMessage());
        verifyNoInteractions(orderRepository);
    }

    @Test
    void getOrderByInvoice_NotFound_ThrowsOrderNotFoundException() {
        when(orderRepository.findByInvoiceId(INVOICE_ID))
                .thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderByInvoice(INVOICE_ID));

        assertEquals("Order not found for invoice id: " + INVOICE_ID, exception.getMessage());
        verify(orderRepository).findByInvoiceId(INVOICE_ID);
    }

    @Test
    void getOrdersByClient_ServiceError_ThrowsOrderServiceException() {
        when(orderRepository.findByOrderedClientId(CLIENT_ID))
                .thenThrow(new RuntimeException("Database error"));

        OrderServiceException exception = assertThrows(
                OrderServiceException.class,
                () -> orderService.getOrdersByClient(CLIENT_ID));

        assertEquals("Failed to retrieve orders for client: " + CLIENT_ID, exception.getMessage());
        verify(orderRepository).findByOrderedClientId(CLIENT_ID);
    }

    @Test
    void getOrdersByInfluencer_ServiceError_ThrowsOrderServiceException() {
        when(orderRepository.findByInfluencerId(INFLUENCER_ID))
                .thenThrow(new RuntimeException("Database error"));

        OrderServiceException exception = assertThrows(
                OrderServiceException.class,
                () -> orderService.getOrdersByInfluencer(INFLUENCER_ID));

        assertEquals("Failed to retrieve orders for influencer: " + INFLUENCER_ID,
                exception.getMessage());
        verify(orderRepository).findByInfluencerId(INFLUENCER_ID);
    }

    @Test
    void getAllOrders_ServiceError_ThrowsOrderServiceException() {
        when(orderRepository.findAll())
                .thenThrow(new RuntimeException("Database error"));

        OrderServiceException exception = assertThrows(
                OrderServiceException.class,
                () -> orderService.getAllOrders());

        assertEquals("Failed to retrieve all orders", exception.getMessage());
        verify(orderRepository).findAll();
    }

    @Test
    void completeOrder_ValidationError_ThrowsInvalidOrderDataException() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));
        doThrow(new InvalidOrderDataException("Invalid completion data"))
                .when(orderValidator).validateCompletion(testOrder, testInfluencer);

        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderService.completeOrder(ORDER_ID, testInfluencer));

        assertEquals("Invalid completion data", exception.getMessage());
        verify(orderRepository).findById(ORDER_ID);
        verify(orderValidator).validateCompletion(testOrder, testInfluencer);
        verifyNoMoreInteractions(orderRepository);
    }
}