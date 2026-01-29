package no.chickendirect.product.dto;

import java.math.BigDecimal;

import no.chickendirect.productstatus.ProductStatus;

public record ProductRequest(
        String name,
        String description,
        BigDecimal price,
        ProductStatus status,
        Integer quantityOnHand
) {}
