package no.chickendirect.customer.dto;

public record CustomerSummaryResponse (
        Long id,
        String name,
        String email
) {}
