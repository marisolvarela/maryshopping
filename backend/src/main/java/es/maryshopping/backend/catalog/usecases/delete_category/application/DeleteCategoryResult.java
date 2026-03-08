package es.maryshopping.backend.catalog.usecases.delete_category.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteCategoryResult(
        UUID id
) {
}
