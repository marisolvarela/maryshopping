package es.maryshopping.backend.catalog.usecases.get_all_providers.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProviderResult(
        UUID id,
        String name
) {
}
