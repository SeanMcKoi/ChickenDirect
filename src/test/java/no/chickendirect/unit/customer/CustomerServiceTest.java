package no.chickendirect.unit.customer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import no.chickendirect.address.Address;
import no.chickendirect.customer.Customer;
import no.chickendirect.customer.CustomerRepository;
import no.chickendirect.customer.CustomerService;
import no.chickendirect.customer.dto.CustomerRequest;
import no.chickendirect.customer.dto.CustomerResponse;
import no.chickendirect.exception.CustomerNotFoundException;
import no.chickendirect.order.Order;
import no.chickendirect.order.dto.OrderSummaryResponse;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_shouldSaveAndReturnResponse() {

        CustomerRequest request = new CustomerRequest("Bob", "12345678", "bob@gmail.com");

        Customer savedEntity = Customer.builder()
                .id(1L)
                .name("Bob")
                .phone("12345678")
                .email("bob@gmail.com")
                .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(savedEntity);

        CustomerResponse response = customerService.createCustomer(request);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        Customer toSave = captor.getValue();

        assertEquals("Bob", toSave.getName());
        assertEquals("12345678", toSave.getPhone());
        assertEquals("bob@gmail.com", toSave.getEmail());
        assertNull(toSave.getId());

        assertEquals(1L, response.id());
        assertEquals("Bob", response.name());
        assertEquals("12345678", response.phone());
        assertEquals("bob@gmail.com", response.email());
        assertTrue(response.addresses().isEmpty());
        assertTrue(response.orders().isEmpty());
    }


    @Test
    void getCustomer_shouldMapAddressesAndOrders() {

        Customer customer = Customer.builder()
                .id(10L)
                .name("Test Customer")
                .phone("99999999")
                .email("test@example.com")
                .build();

        Address address = Address.builder()
                .id(5L)
                .street("Street 1")
                .city("Oslo")
                .postalCode("0001")
                .country("Norway")
                .customer(customer)
                .build();

        Order order = Order.builder()
                .id(7L)
                .totalPrice(BigDecimal.valueOf(100))
                .shippingCharge(BigDecimal.valueOf(20))
                .isShipped(false)
                .creationDate(LocalDateTime.now())
                .customer(customer)
                .build();

        customer.getAddresses().add(address);
        customer.getOrders().add(order);

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getCustomer(10L);

        assertEquals(10L, response.id());
        assertEquals(1, response.addresses().size());
        assertEquals(1, response.orders().size());

        var addr = response.addresses().get(0);
        assertEquals("Oslo", addr.city());

        OrderSummaryResponse orderSummary = response.orders().get(0);
        assertEquals(7L, orderSummary.id());
        assertEquals(BigDecimal.valueOf(100), orderSummary.totalPrice());
    }

    @Test
    void getCustomer_notFound_shouldThrow() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomer(99L));
        assertNotNull(exception);
    }
    @Test
    void deleteCustomer_notExisting_shouldThrow() {
        when(customerRepository.existsById(42L)).thenReturn(false);

        Exception exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.deleteCustomer(42L));
        assertNotNull(exception);
    }
}
