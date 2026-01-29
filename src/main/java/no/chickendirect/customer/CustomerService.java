package no.chickendirect.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import no.chickendirect.address.Address;
import no.chickendirect.address.dto.AddressResponse;
import no.chickendirect.customer.dto.CustomerRequest;
import no.chickendirect.customer.dto.CustomerResponse;
import no.chickendirect.order.Order;
import no.chickendirect.order.dto.OrderSummaryResponse;
import no.chickendirect.exception.CustomerNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer getCustomerEntity(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Creating customer with email={}", request.email());
        Customer customer = Customer.builder()
                .name(request.name())
                .phone(request.phone())
                .email(request.email())
                .build();

        Customer saved = customerRepository.save(customer);
        return toCustomerResponse(saved);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long id) {
        Customer customer = getCustomerEntity(id);
        return toCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toCustomerResponse)
                .toList();
    }


    public CustomerResponse updateCustomer(
            Long id,
            @Valid CustomerRequest request) {
        log.info("Updating customer with id={}", id);

        Customer customer = getCustomerEntity(id);

        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());

        Customer updated = customerRepository.save(customer);

        return toCustomerResponse(updated);
    }

    public void deleteCustomer(Long id) {
        log.info("Deleting customer with id={}", id);
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponse toCustomerResponse(Customer customer) {

        var addressDtos = customer.getAddresses().stream()
                .map(this::toAddressResponse)
                .toList();

        var orderDtos = customer.getOrders().stream()
                .map(this::toOrderSummaryResponse)
                .toList();

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                addressDtos,
                orderDtos
        );
    }

    private AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getPostalCode(),
                address.getCountry()
        );
    }

    private OrderSummaryResponse toOrderSummaryResponse(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getShippingCharge(),
                order.getIsShipped(),
                order.getCreationDate()
        );
    }
}
