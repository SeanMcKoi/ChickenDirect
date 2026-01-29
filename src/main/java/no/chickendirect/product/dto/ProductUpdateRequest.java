package no.chickendirect.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.PositiveOrZero;
import no.chickendirect.productstatus.ProductStatus;

public record ProductUpdateRequest(
        String name,
        String description,
        @PositiveOrZero BigDecimal price,
        ProductStatus status,
        @PositiveOrZero Integer quantityOnHand
) {}
