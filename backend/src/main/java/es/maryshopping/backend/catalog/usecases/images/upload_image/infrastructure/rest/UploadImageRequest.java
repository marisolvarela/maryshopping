package es.maryshopping.backend.catalog.usecases.images.upload_image.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UploadImageRequest(
        byte[] bytes,
        String mimeType,
        int order,
        boolean primary,
        UUID productId
) {
}
