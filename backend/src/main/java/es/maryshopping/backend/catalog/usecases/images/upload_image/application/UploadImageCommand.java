package es.maryshopping.backend.catalog.usecases.images.upload_image.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UploadImageCommand(
        byte[] bytes,
        String mimeType,
        int order,
        boolean primary,
        UUID productId
) {
}
