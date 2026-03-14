package es.maryshopping.backend.catalog.usecases.get_all_providers.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProviderResponse(
        UUID id,
        String name
) {
}
