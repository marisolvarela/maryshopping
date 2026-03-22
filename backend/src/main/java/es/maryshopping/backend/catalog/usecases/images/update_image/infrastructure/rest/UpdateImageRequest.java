package es.maryshopping.backend.catalog.usecases.images.update_image.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;
@Builder
public record UpdateImageRequest(
        int order,
        boolean primary,
        UUID productId
) {
}
