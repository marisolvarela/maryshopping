package es.maryshopping.backend.catalog.usecases.delete_category.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteCategoryCommand(
        UUID id
) {
}
