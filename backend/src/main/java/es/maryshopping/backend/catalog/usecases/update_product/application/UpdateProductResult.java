package es.maryshopping.backend.catalog.usecases.update_product.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateProductResult(
        UUID id
) {
}
