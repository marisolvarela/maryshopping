package es.maryshopping.backend.catalog.usecases.get_products_in_category.application;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProductInCategoryResult(
        UUID id,
        String name,
        CategoryResult category,
        String description,
        ProviderResult provider,
        String providerReference,
        BigDecimal price
) {
    @Builder
    public record CategoryResult(UUID id, String name) {
    }

    @Builder
    public record ProviderResult(UUID id, String name) {
    }
}

