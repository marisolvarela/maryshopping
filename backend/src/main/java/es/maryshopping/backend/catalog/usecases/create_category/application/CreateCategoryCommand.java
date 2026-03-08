package es.maryshopping.backend.catalog.usecases.create_category.application;

import lombok.Builder;

@Builder
public record CreateCategoryCommand(
        String name
) {
}
