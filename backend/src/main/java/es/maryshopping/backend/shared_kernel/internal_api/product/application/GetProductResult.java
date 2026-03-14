package es.maryshopping.backend.shared_kernel.internal_api.product.application;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record GetProductResult(
        UUID id,
        String name,
        CategoryResult category,
        String description,
        ProviderResult provider,
        String providerReference,
        BigDecimal price
) {
    @Builder
public record CategoryResult(UUID id, String name){

    }
    @Builder
public record ProviderResult(UUID id, String name){

    }
}
