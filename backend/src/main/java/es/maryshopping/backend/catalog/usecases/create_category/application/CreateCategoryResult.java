package es.maryshopping.backend.catalog.usecases.create_category.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCategoryResult(
        UUID id
) {
}
