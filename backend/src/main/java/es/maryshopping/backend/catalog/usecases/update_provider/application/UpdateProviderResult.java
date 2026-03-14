package es.maryshopping.backend.catalog.usecases.update_provider.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateProviderResult(
        UUID id
) {
}
