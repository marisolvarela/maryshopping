package es.maryshopping.backend.catalog.usecases.get_provider.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetProviderResponse(
        UUID id,
        String name
) {
}
