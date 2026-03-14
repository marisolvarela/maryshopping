package es.maryshopping.backend.catalog.usecases.update_product.infrastructure.rest;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
        String name,
        UUID categoryId,
        String description,
        UUID provider,
        String providerReference,
        BigDecimal price
) {
}
