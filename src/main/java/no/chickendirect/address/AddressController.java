package no.chickendirect.address;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.chickendirect.address.dto.AddressRequest;
import no.chickendirect.address.dto.AddressResponse;
import no.chickendirect.customer.Customer;
import no.chickendirect.customer.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/customers/{customerId}/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse addAddress(
            @PathVariable Long customerId,
            @RequestBody AddressRequest request
    ) {
        Customer customer = customerService.getCustomerEntity(customerId);
        return addressService.addAddressToCustomer(customer, request);
    }

    @GetMapping
    public List<AddressResponse> getAddresses(@PathVariable Long customerId) {
        Customer customer = customerService.getCustomerEntity(customerId);
        return addressService.getAddressesForCustomer(customer);
    }

    @PutMapping("/{addressId}")
    public AddressResponse updateAddress(
            @PathVariable Long customerId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request
    ) {
        return addressService.updateAddress(addressId, customerId, request);
    }

    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(
            @PathVariable Long customerId,
            @PathVariable Long addressId
    ) {
        addressService.deleteAddress(addressId, customerId);
    }


}
