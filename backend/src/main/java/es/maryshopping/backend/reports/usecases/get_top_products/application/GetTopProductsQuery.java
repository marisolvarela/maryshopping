package es.maryshopping.backend.reports.usecases.get_top_products.application;

import lombok.Builder;

@Builder
public record GetTopProductsQuery(
        Integer limit
) {
}

