package es.maryshopping.backend.catalog.usecases.images.get_product_images.infrastrucutre.rest;

import es.maryshopping.backend.catalog.usecases.images.get_product_images.application.GetProductsImagesQuery;
import es.maryshopping.backend.catalog.usecases.images.get_product_images.application.GetProductsImagesService;
import es.maryshopping.backend.catalog.usecases.images.get_product_images.application.ImageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog/products/images")
public class GetProductsImagesController {
    private final GetProductsImagesService getProductsImagesService;

    public GetProductsImagesController(GetProductsImagesService getProductsImagesService) {
        this.getProductsImagesService = getProductsImagesService;
    }

    @GetMapping
    public ResponseEntity<List<ImageResponse>> proceed(){
        GetProductsImagesQuery query = createQuery();
        List<ImageResult> imageResults = getProductsImagesService.proceed(query);
        List<ImageResponse> imageResponses = imageResults.stream()
                .map(this::mapFromResultToResponse)
                .toList();
        return ResponseEntity.status(200).body(imageResponses);
    }
    private GetProductsImagesQuery createQuery(){
        return new GetProductsImagesQuery();
    }
    private ImageResponse mapFromResultToResponse(ImageResult result){
        return ImageResponse.builder()
                .id(result.id())
                .productId(result.productId())
                .mimeType(result.imageMimeType())
                .order(result.imageOrder())
                .isPrimary(result.isPrimary())
                .build();
    }

}
