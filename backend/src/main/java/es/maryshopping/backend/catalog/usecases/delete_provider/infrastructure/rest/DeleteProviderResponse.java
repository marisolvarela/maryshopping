package es.maryshopping.backend.catalog.usecases.delete_provider.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteProviderResponse(
        UUID id
) {
}
