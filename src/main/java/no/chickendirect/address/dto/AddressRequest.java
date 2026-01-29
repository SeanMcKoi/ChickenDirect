package no.chickendirect.address.dto;

public record AddressRequest (
        String street,
        String city,
        String postalCode,
        String country
) {}
