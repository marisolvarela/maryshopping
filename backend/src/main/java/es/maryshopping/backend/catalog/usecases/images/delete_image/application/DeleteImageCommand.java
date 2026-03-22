package es.maryshopping.backend.catalog.usecases.images.delete_image.application;

import java.util.UUID;

public record DeleteImageCommand(
        UUID id
) {
}
