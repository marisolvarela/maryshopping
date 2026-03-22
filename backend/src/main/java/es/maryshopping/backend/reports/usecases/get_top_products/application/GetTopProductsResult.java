package es.maryshopping.backend.reports.usecases.get_top_products.application;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record GetTopProductsResult(
        UUID productId,
        String productName,
        String productProviderReference,
        String categoryName,
        String providerName,
        Long totalUnitsSold,
        BigDecimal totalRevenue
) {
}

