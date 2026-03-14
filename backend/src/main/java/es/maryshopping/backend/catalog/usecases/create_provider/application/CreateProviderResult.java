package es.maryshopping.backend.catalog.usecases.create_provider.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateProviderResult(
        UUID id
) {
}
