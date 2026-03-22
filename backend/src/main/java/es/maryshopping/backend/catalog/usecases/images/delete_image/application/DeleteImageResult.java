package es.maryshopping.backend.catalog.usecases.images.delete_image.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteImageResult(
        UUID id
) {
}
