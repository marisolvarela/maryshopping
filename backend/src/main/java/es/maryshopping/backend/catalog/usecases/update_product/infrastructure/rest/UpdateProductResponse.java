package es.maryshopping.backend.catalog.usecases.update_product.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateProductResponse(
        UUID id
) {
}
