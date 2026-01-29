package no.chickendirect.address.dto;

public record AddressResponse (
        Long id,
        String street,
        String city,
        String postalCode,
        String country
){}
