package no.chickendirect.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long id,
        BigDecimal totalPrice,
        BigDecimal shippingCharge,
        Boolean isShipped,
        LocalDateTime creationDate
) {}
