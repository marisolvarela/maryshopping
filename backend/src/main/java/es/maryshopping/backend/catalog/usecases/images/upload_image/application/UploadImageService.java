package es.maryshopping.backend.catalog.usecases.images.upload_image.application;

import es.maryshopping.backend.catalog.shared.domain.product.ProductId;
import es.maryshopping.backend.catalog.shared.domain.product.image.*;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ImageEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ImageSpringRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UploadImageService {
    private final ImageSpringRepository repository;

    public UploadImageService(ImageSpringRepository repository) {
        this.repository = repository;
    }

    public UploadImageResult proceed(UploadImageCommand command) {
        UUID imageId = UUID.randomUUID();
        Image image = fromCommandToDomain(imageId, command);
        ImageEntity entity = ImageEntity.fromDomain(image);
        ImageEntity saved = repository.save(entity);
        return UploadImageResult.builder().imageId(saved.getId()).build();
    }

    private Image fromCommandToDomain(UUID imageId, UploadImageCommand command) {
        return Image.builder()
                .id(new ImageId(imageId))
                .productId(new ProductId(command.productId()))
                .bytes(new ImageBytes(command.bytes()))
                .mimeType(new ImageMimeType(command.mimeType()))
                .order(new ImageOrder(command.order()))
                .primary(new IsPrimary(command.primary()))
                .build();
    }
}
