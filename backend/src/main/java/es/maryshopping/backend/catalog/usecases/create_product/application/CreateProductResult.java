package es.maryshopping.backend.catalog.usecases.create_product.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateProductResult(
        UUID productId
) {
}
