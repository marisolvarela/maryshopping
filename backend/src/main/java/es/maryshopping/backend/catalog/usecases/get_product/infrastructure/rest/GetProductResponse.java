package es.maryshopping.backend.catalog.usecases.get_product.infrastructure.rest;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record GetProductResponse(
        UUID id,
        String name,
        CategoryResponse category,
        String description,
        ProviderResponse provider,
        String providerReference,
        BigDecimal price
) {
    @Builder
public record CategoryResponse(UUID id, String name){

    }
    @Builder
public record ProviderResponse(UUID id, String name){

    }
}

