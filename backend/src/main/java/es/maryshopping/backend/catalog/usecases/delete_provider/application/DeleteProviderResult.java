package es.maryshopping.backend.catalog.usecases.delete_provider.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteProviderResult(
        UUID id
) {
}
