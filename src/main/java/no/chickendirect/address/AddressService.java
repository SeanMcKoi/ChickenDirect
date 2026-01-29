package no.chickendirect.address;

import lombok.RequiredArgsConstructor;
import no.chickendirect.address.dto.AddressRequest;
import no.chickendirect.address.dto.AddressResponse;
import no.chickendirect.customer.Customer;
import no.chickendirect.exception.AddressNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressResponse addAddressToCustomer(Customer customer, AddressRequest request) {
        Address address = Address.builder()
                .street(request.street())
                .city(request.city())
                .postalCode(request.postalCode())
                .country(request.country())
                .customer(customer)
                .build();

        Address saved = addressRepository.save(address);
        return toAddressResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesForCustomer(Customer customer) {
        return addressRepository.findByCustomer(customer).stream()
                .map(this::toAddressResponse)
                .toList();
    }


    public AddressResponse updateAddress(Long addressId, Long customerId, AddressRequest request) {
        Address address = getAddressEntity(addressId);
        verifyAddressBelongsToCustomer(address, customerId);

        address.setStreet(request.street());
        address.setCity(request.city());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());

        Address saved = addressRepository.save(address);
        return toAddressResponse(saved);
    }

    public void deleteAddress(Long addressId, Long customerId) {
        Address address = getAddressEntity(addressId);
        verifyAddressBelongsToCustomer(address, customerId);
        addressRepository.delete(address);
    }

    public Address getAddressEntity(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));
    }

    public void verifyAddressBelongsToCustomer(Address address, Long customerId) {
        if (!address.getCustomer().getId().equals(customerId)) {
            throw new AddressNotFoundException(address.getId());
        }
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
}
