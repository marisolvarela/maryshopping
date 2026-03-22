package es.maryshopping.backend.catalog.usecases.images.get_image.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ImageEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ImageSpringRepository;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProductEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProductSpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetImageService {
    private final ImageSpringRepository repository;
    private final ProductSpringRepository productRepository;

    public GetImageService(ImageSpringRepository repository, ProductSpringRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
    }

    public GetImageResult proceed(GetImageQuery query) {
        Optional<ImageEntity> optionalImageEntity = repository.findById(query.id());
        if (optionalImageEntity.isEmpty()) {
            throw new EntityNotFoundException("Image not found");
        }
        ImageEntity entity = optionalImageEntity.get();
        Optional<ProductEntity> productEntityOptional = productRepository.findById(entity.getProductId());
        if (productEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("Product not found");
        }
        return GetImageResult.builder()
                .imageId(entity.getId())
                .bytes(entity.getImageBytes())
                .mimeType(entity.getImageMimeType())
                .order(entity.getImageOrder())
                .primary(entity.getIsPrimary())
                .productId(entity.getProductId())
                .build();
    }
}
