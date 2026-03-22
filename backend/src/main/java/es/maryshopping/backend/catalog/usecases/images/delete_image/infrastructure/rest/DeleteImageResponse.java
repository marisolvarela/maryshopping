package es.maryshopping.backend.catalog.usecases.images.delete_image.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteImageResponse(
        UUID id
) {
}
