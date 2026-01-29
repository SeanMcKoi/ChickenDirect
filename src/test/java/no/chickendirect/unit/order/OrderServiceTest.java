package no.chickendirect.unit.order;

import no.chickendirect.order.*;
import no.chickendirect.address.Address;
import no.chickendirect.address.AddressService;
import no.chickendirect.customer.Customer;
import no.chickendirect.customer.CustomerService;
import no.chickendirect.exception.AddressNotFoundException;
import no.chickendirect.exception.CustomerNotFoundException;
import no.chickendirect.exception.OrderNotFoundException;
import no.chickendirect.order.dto.OrderRequest;
import no.chickendirect.order.dto.OrderResponse;
import no.chickendirect.order.dto.OrderUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private AddressService addressService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldSaveAndReturnResponse() {
        OrderRequest request = new OrderRequest(
                1L,
                2L,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10),
                false
        );

        Customer customer = Customer.builder().id(1L).name("Bob").email("bob@example.com").phone("12345678").build();
        Address address = Address.builder().id(2L).street("Street").city("City").postalCode("1234").country("Norway").build();

        when(customerService.getCustomerEntity(1L)).thenReturn(customer);
        when(addressService.getAddressEntity(2L)).thenReturn(address);

        Order savedOrder = Order.builder()
                .id(10L)
                .customer(customer)
                .customerName("Bob")
                .customerEmail("bob@example.com")
                .customerPhone("12345678")
                .shippingStreet("Street")
                .shippingCity("City")
                .shippingPostalCode("1234")
                .shippingCountry("Norway")
                .totalPrice(BigDecimal.valueOf(100))
                .shippingCharge(BigDecimal.valueOf(10))
                .isShipped(false)
                .creationDate(LocalDateTime.now())
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order toSave = captor.getValue();

        assertEquals(customer, toSave.getCustomer());
        assertEquals("Bob", toSave.getCustomerName());
        assertEquals("bob@example.com", toSave.getCustomerEmail());
        assertEquals("12345678", toSave.getCustomerPhone());
        assertEquals("Street", toSave.getShippingStreet());
        assertEquals("City", toSave.getShippingCity());
        assertEquals(BigDecimal.valueOf(100), toSave.getTotalPrice());

        assertEquals(10L, response.id());
        assertEquals("Bob", response.customer().name());
        assertEquals("City", response.shippingCity());
    }

    @Test
    void createOrder_customerNotFound_shouldThrowException() {
        OrderRequest request = new OrderRequest(99L, 2L, BigDecimal.TEN, BigDecimal.ONE, false);
        when(customerService.getCustomerEntity(99L)).thenThrow(new CustomerNotFoundException(99L));

        assertThrows(CustomerNotFoundException.class, () -> orderService.createOrder(request));
    }

    @Test
    void createOrder_addressNotFound_shouldThrowException() {
        OrderRequest request = new OrderRequest(1L, 99L, BigDecimal.TEN, BigDecimal.ONE, false);
        Customer customer = Customer.builder().id(1L).build();

        when(customerService.getCustomerEntity(1L)).thenReturn(customer);
        when(addressService.getAddressEntity(99L)).thenThrow(new AddressNotFoundException(99L));

        assertThrows(AddressNotFoundException.class, () -> orderService.createOrder(request));
    }

    @Test
    void getOrder_shouldReturnResponse() {
        Customer customer = Customer.builder().id(1L).name("Bob").email("bob@example.com").build();
        Order order = Order.builder()
                .id(10L)
                .customer(customer)
                .shippingStreet("Street")
                .shippingCity("City")
                .shippingPostalCode("1234")
                .shippingCountry("Norway")
                .totalPrice(BigDecimal.valueOf(100))
                .shippingCharge(BigDecimal.valueOf(10))
                .isShipped(false)
                .creationDate(LocalDateTime.now())
                .build();

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrder(10L);

        assertEquals(10L, response.id());
    }

    @Test
    void getOrder_notFound_shouldThrowException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(99L));
    }

    @Test
    void deleteOrder_shouldDeleteIfFound() {
        when(orderRepository.existsById(10L)).thenReturn(true);
        orderService.deleteOrder(10L);
        verify(orderRepository).deleteById(10L);
    }

    @Test
    void deleteOrder_notFound_shouldThrowException() {
        when(orderRepository.existsById(99L)).thenReturn(false);
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(99L));
    }

    @Test
    void updateOrder_shouldUpdateAndReturnResponse() {
        OrderUpdateRequest request = new OrderUpdateRequest(
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(15),
                true
        );

        Customer customer = Customer.builder().id(1L).name("Bob").email("bob@example.com").build();
        Order existingOrder = Order.builder()
                .id(10L)
                .customer(customer)
                .shippingStreet("Street")
                .shippingCity("City")
                .shippingPostalCode("1234")
                .shippingCountry("Norway")
                .totalPrice(BigDecimal.valueOf(100))
                .shippingCharge(BigDecimal.valueOf(10))
                .isShipped(false)
                .creationDate(LocalDateTime.now())
                .build();

        when(orderRepository.findById(10L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        OrderResponse response = orderService.updateOrder(10L, request);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order updatedOrder = captor.getValue();

        assertEquals(BigDecimal.valueOf(150), updatedOrder.getTotalPrice());
        assertEquals(BigDecimal.valueOf(15), updatedOrder.getShippingCharge());
        assertTrue(updatedOrder.getIsShipped());
        assertEquals(10L, response.id());
    }

    @Test
    void updateOrder_onlyShippedStatus_shouldUpdateOnlyIsShipped() {

        Customer customer = Customer.builder().id(1L).name("Bob").email("bob@example.com").build();
        Order existingOrder = Order.builder()
                .id(10L)
                .customer(customer)
                .shippingStreet("Street")
                .shippingCity("City")
                .shippingPostalCode("1234")
                .shippingCountry("Norway")
                .totalPrice(BigDecimal.valueOf(100))
                .shippingCharge(BigDecimal.valueOf(10))
                .isShipped(false)
                .creationDate(LocalDateTime.now())
                .build();

        when(orderRepository.findById(10L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        OrderUpdateRequest request = new OrderUpdateRequest(null, null, true);
        orderService.updateOrder(10L, request);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order updatedOrder = captor.getValue();

        assertEquals(BigDecimal.valueOf(100), updatedOrder.getTotalPrice());
        assertEquals(BigDecimal.valueOf(10), updatedOrder.getShippingCharge());
        assertTrue(updatedOrder.getIsShipped());
    }

    @Test
    void updateOrder_notFound_shouldThrowException() {
        OrderUpdateRequest request = new OrderUpdateRequest(null, null, true);
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(99L, request));
    }}