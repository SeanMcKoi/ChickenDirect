package no.chickendirect.order.dto;

import no.chickendirect.customer.dto.CustomerSummaryResponse;
import no.chickendirect.orderitem.dto.OrderItemResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        CustomerSummaryResponse customer,
        String shippingStreet,
        String shippingCity,
        String shippingPostalCode,
        String shippingCountry,
        BigDecimal totalPrice,
        BigDecimal shippingCharge,
        Boolean isShipped,
        LocalDateTime creationDate,
        List<OrderItemResponse> items
) {}
