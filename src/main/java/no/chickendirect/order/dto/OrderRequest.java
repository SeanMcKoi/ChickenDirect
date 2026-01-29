package no.chickendirect.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record OrderRequest(
        @NotNull Long customerId,
        @NotNull Long shippingAddressId,
        @NotNull @PositiveOrZero BigDecimal totalPrice,
        @NotNull @PositiveOrZero BigDecimal shippingCharge,
        Boolean isShipped
) {}
