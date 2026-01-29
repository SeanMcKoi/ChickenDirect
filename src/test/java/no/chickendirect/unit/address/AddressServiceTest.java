package no.chickendirect.unit.address;

import no.chickendirect.address.*;
import no.chickendirect.address.dto.AddressRequest;
import no.chickendirect.address.dto.AddressResponse;
import no.chickendirect.customer.Customer;
import no.chickendirect.exception.AddressNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @Test
    void addAddressToCustomer_shouldCreateAndReturnDto() {
        Customer customer = Customer.builder()
                .id(1L)
                .name("Bob")
                .build();

        AddressRequest request = new AddressRequest(
                "Street 1", "Oslo", "0001", "Norway"
        );

        Address saved = Address.builder()
                .id(10L)
                .street("Bogstadveien 1")
                .city("Oslo")
                .postalCode("0355")
                .country("Norway")
                .customer(customer)
                .build();

        when(addressRepository.save(any(Address.class))).thenReturn(saved);

        AddressResponse response = addressService.addAddressToCustomer(customer, request);

        verify(addressRepository).save(any(Address.class));
        assertEquals(10L, response.id());
        assertEquals("Oslo", response.city());
    }

    @Test
    void updateAddress_wrongCustomer_shouldThrowNotFound() {
        Customer owner = Customer.builder().id(1L).build();

        Address existing = Address.builder()
                .id(5L)
                .street("Old street")
                .city("Old city")
                .postalCode("1234")
                .country("Norway")
                .customer(owner)
                .build();

        when(addressRepository.findById(5L)).thenReturn(Optional.of(existing));

        AddressRequest req = new AddressRequest(
                "New street", "New city", "9999", "Norway"
        );

        assertThrows(AddressNotFoundException.class,
                () -> addressService.updateAddress(5L, 2L, req));
    }

    @Test
    void deleteAddress_notFound_shouldThrow() {
        when(addressRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class,
                () -> addressService.deleteAddress(100L, 1L));
    }
}
