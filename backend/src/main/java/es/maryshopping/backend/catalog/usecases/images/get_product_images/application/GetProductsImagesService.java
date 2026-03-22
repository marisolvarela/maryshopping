package es.maryshopping.backend.catalog.usecases.images.get_product_images.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ImageEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ImageSpringRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetProductsImagesService {
    private final ImageSpringRepository repository;

    public GetProductsImagesService(ImageSpringRepository repository) {
        this.repository = repository;
    }
    public List<ImageResult> proceed(GetProductsImagesQuery query){
        List<ImageEntity> imageEntities = repository.findAll();
        return imageEntities.stream()
                .map(this::fromEntityToResult)
                .toList();
    }
    private ImageResult fromEntityToResult(ImageEntity imageEntity){
        return ImageResult.builder()
                .id(imageEntity.getId())
                .productId(imageEntity.getProductId())
                .imageMimeType(imageEntity.getImageMimeType())
                .imageOrder(imageEntity.getImageOrder())
                .isPrimary(imageEntity.getIsPrimary())
                .imageBytes(imageEntity.getImageBytes())
                .build();
    }
}
