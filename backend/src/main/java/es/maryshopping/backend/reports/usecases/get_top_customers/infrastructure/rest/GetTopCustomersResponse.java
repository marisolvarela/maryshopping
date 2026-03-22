package es.maryshopping.backend.reports.usecases.get_top_customers.infrastructure.rest;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record GetTopCustomersResponse(
        UUID customerId,
        String firstName,
        String lastName,
        String dni,
        BigDecimal totalSpent
) {
}

