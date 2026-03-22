package es.maryshopping.backend.catalog.usecases.images.get_product_images.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ImageResult(
        UUID id,
        UUID productId,
        String imageMimeType,
        Integer imageOrder,
        Boolean isPrimary,
        byte[] imageBytes
) {
}
