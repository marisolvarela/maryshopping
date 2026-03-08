package es.maryshopping.backend.catalog.usecases.get_category.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetCategoryResponse(
        UUID id,
        String name

) {
}
