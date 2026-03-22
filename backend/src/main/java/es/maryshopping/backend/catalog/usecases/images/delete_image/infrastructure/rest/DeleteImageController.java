package es.maryshopping.backend.catalog.usecases.images.delete_image.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.images.delete_image.application.DeleteImageCommand;
import es.maryshopping.backend.catalog.usecases.images.delete_image.application.DeleteImageResult;
import es.maryshopping.backend.catalog.usecases.images.delete_image.application.DeleteImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/images")
public class DeleteImageController {
    private final DeleteImageService deleteImageService;

    public DeleteImageController(DeleteImageService deleteImageService) {
        this.deleteImageService = deleteImageService;
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<DeleteImageResponse> proceed(@PathVariable UUID imageId) {
        DeleteImageCommand command = mapFromPathToCommand(imageId);
        DeleteImageResult result = deleteImageService.proceed(command);
        DeleteImageResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private DeleteImageCommand mapFromPathToCommand(UUID imageId) {
        return new DeleteImageCommand(imageId);
    }

    private DeleteImageResponse mapFromResultToResponse(DeleteImageResult result) {
        return DeleteImageResponse.builder().id(result.id()).build();
    }
}
