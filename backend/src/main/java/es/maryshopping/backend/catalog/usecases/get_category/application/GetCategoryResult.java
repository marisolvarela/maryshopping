package es.maryshopping.backend.catalog.usecases.get_category.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetCategoryResult(
        UUID id,
        String name
) {
}
