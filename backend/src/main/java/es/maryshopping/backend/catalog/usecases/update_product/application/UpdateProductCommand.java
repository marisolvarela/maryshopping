package es.maryshopping.backend.catalog.usecases.update_product.application;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record UpdateProductCommand(
        UUID id,
        String name,
        UUID categoryId,
        String description,
        UUID provider,
        String providerReference,
        BigDecimal price
) {
}
