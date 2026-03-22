package es.maryshopping.backend.catalog.usecases.images.update_image.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.images.update_image.application.UpdateImageCommand;
import es.maryshopping.backend.catalog.usecases.images.update_image.application.UpdateImageResult;
import es.maryshopping.backend.catalog.usecases.images.update_image.application.UpdateImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/products/images")
public class UpdateImageController {
    private final UpdateImageService updateImageService;

    public UpdateImageController(UpdateImageService updateImageService) {
        this.updateImageService = updateImageService;
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<UpdateImageResponse> proceed(@PathVariable UUID imageId, @RequestBody UpdateImageRequest request) {
        UpdateImageCommand command = mapFromRequestToCommand(imageId, request);
        UpdateImageResult result = updateImageService.proceed(command);
        UpdateImageResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private UpdateImageCommand mapFromRequestToCommand(UUID imageId, UpdateImageRequest request) {
        return UpdateImageCommand.builder()
                .id(imageId)
                .order(request.order())
                .primary(request.primary())
                .productId(request.productId())
                .build();
    }
    private UpdateImageResponse mapFromResultToResponse(UpdateImageResult result){
        return UpdateImageResponse.builder().id(result.id()).build();
    }
}
