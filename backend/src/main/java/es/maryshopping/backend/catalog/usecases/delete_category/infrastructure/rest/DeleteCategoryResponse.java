package es.maryshopping.backend.catalog.usecases.delete_category.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteCategoryResponse(
        UUID id
) {
}
