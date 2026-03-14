package es.maryshopping.backend.catalog.usecases.update_provider.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateProviderResponse(
        UUID id
) {
}
