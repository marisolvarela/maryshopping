package es.maryshopping.backend.catalog.usecases.images.get_image.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.images.get_image.application.GetImageQuery;
import es.maryshopping.backend.catalog.usecases.images.get_image.application.GetImageResult;
import es.maryshopping.backend.catalog.usecases.images.get_image.application.GetImageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/catalog/products/images")
public class GetImageController {
    private final GetImageService getImageService;

    public GetImageController(GetImageService getImageService) {
        this.getImageService = getImageService;
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> proceed(@PathVariable UUID imageId) {
        GetImageQuery query = mapFromPathToQuery(imageId);
        GetImageResult result = getImageService.proceed(query);
        //Hay que devolver los bytes de la imagen en trozos y no en un json, por eso se utiliza ByteArrayResource
        // que es un recurso de Spring que permite devolver un array de bytes como respuesta HTTP.
        ByteArrayResource resource = new ByteArrayResource(result.bytes());
        return ResponseEntity
                .status(200)
                .contentType(MediaType.parseMediaType(result.mimeType()))
                .contentLength(result.bytes().length)
                .cacheControl(CacheControl.maxAge(Duration.ofHours(24)).cachePublic())
                .body(resource);
    }

    private GetImageQuery mapFromPathToQuery(UUID imageId) {
        return new GetImageQuery(imageId);
    }

}
