package es.maryshopping.backend.catalog.usecases.images.get_image.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetImageResult(
        UUID imageId,
        byte[] bytes,
        String mimeType,
        int order,
        boolean primary,
        UUID productId
) {
}
