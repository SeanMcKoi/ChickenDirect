package no.chickendirect.orderitem.dto;

import no.chickendirect.product.dto.ProductResponse;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Integer quantity,
        ProductResponse product,
        Long orderId,
        BigDecimal lineTotal
) {
}
