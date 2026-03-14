package es.maryshopping.backend.catalog.usecases.delete_product.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteProductCommand(
        UUID id
) {
}
