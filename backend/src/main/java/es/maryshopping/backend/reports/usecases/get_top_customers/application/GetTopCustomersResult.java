package es.maryshopping.backend.reports.usecases.get_top_customers.application;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record GetTopCustomersResult(
        UUID customerId,
        String firstName,
        String lastName,
        String dni,
        BigDecimal totalSpent
) {
}

