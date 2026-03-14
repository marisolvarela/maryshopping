package es.maryshopping.backend.catalog.usecases.delete_product.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteProductResponse(
        UUID id
) {
}
