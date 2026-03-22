package es.maryshopping.backend.catalog.usecases.images.upload_image.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UploadImageResult(
        UUID imageId
) {
}
