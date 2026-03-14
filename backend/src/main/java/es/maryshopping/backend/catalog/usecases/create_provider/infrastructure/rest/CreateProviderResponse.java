package es.maryshopping.backend.catalog.usecases.create_provider.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateProviderResponse(
        UUID id
) {
}
