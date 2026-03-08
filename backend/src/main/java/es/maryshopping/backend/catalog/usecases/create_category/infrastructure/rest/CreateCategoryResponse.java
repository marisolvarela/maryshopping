package es.maryshopping.backend.catalog.usecases.create_category.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCategoryResponse(
        UUID id
) {
}
