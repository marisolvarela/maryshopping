package es.maryshopping.backend.catalog.usecases.create_provider.application;

import lombok.Builder;

@Builder
public record CreateProviderCommand(
        String name
) {
}
