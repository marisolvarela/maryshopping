package es.maryshopping.backend.catalog.usecases.update_category.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateCategoryCommand(
        UUID id,
        String name
) {
}
