package no.chickendirect.product.dto;

import java.math.BigDecimal;

import no.chickendirect.productstatus.ProductStatus;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        ProductStatus status,
        Integer quantityOnHand
) {}
