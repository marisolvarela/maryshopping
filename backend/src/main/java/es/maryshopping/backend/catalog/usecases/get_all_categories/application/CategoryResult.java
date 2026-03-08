package es.maryshopping.backend.catalog.usecases.get_all_categories.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CategoryResult(
        UUID id,
        String name
) {
}
