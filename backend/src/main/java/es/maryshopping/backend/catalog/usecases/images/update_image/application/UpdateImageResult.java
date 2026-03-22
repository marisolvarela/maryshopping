package es.maryshopping.backend.catalog.usecases.images.update_image.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateImageResult(
        UUID id
) {
}
