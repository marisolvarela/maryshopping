package es.maryshopping.backend.catalog.usecases.create_product.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateProductResponse(
        UUID productId
) {
}
