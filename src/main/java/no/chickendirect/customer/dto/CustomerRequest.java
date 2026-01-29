package no.chickendirect.customer.dto;

public record CustomerRequest (
        String name,
        String phone,
        String email
) {}
