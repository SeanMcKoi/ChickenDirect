package no.chickendirect.customer.dto;

import no.chickendirect.address.dto.AddressResponse;
import no.chickendirect.order.dto.OrderSummaryResponse;

import java.util.List;

public record CustomerResponse (
        Long id,
        String name,
        String phone,
        String email,
        List<AddressResponse> addresses,
        List<OrderSummaryResponse> orders
) {}
