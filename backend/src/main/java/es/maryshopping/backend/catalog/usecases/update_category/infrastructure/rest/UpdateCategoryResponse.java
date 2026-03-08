package es.maryshopping.backend.catalog.usecases.update_category.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateCategoryResponse(
        UUID id

) {
}
