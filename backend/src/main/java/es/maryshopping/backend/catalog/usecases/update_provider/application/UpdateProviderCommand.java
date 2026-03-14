package es.maryshopping.backend.catalog.usecases.update_provider.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateProviderCommand(
        UUID id,
        String name
) {
}
