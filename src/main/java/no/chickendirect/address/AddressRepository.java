package no.chickendirect.address;

import no.chickendirect.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByCustomer(Customer customer);
}
