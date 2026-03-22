package es.maryshopping.backend.reports.usecases.get_top_customers.application;

import lombok.Builder;

@Builder
public record GetTopCustomersQuery(
        Integer limit
) {
}

