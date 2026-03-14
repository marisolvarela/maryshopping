package es.maryshopping.backend.catalog.usecases.create_product.application;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CreateProductCommand(
        String name,
        UUID categoryId,
        String description,
        UUID providerId,
        String providerReference,
        BigDecimal price
) {
}

