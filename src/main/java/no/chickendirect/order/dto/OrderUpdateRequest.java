package no.chickendirect.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record OrderUpdateRequest(
        @PositiveOrZero BigDecimal totalPrice,
        @PositiveOrZero BigDecimal shippingCharge,
        @NotNull Boolean isShipped
) {}
