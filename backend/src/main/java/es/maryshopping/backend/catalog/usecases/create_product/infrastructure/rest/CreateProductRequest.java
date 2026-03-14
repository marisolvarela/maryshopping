package es.maryshopping.backend.catalog.usecases.create_product.infrastructure.rest;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        String name,
        UUID categoryId,
        String description,
        UUID providerId,
        String providerReference,
        BigDecimal price
) {
}
