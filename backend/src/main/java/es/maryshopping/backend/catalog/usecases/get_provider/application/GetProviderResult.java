package es.maryshopping.backend.catalog.usecases.get_provider.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetProviderResult(
        UUID id,
        String name
) {
}
