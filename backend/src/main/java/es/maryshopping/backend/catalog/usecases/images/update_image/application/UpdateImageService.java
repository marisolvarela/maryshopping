package es.maryshopping.backend.catalog.usecases.images.update_image.application;

import es.maryshopping.backend.catalog.shared.domain.product.ProductId;
import es.maryshopping.backend.catalog.shared.domain.product.image.*;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ImageEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ImageSpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateImageService {
    private final ImageSpringRepository repository;

    public UpdateImageService(ImageSpringRepository repository) {
        this.repository = repository;
    }
    public UpdateImageResult proceed(UpdateImageCommand command){
        Optional<ImageEntity> optionalImageEntity = repository.findById(command.id());
        if (optionalImageEntity.isEmpty()) {
            throw new EntityNotFoundException("Image not found");
        }
        ImageEntity imageEntityCurrenty = optionalImageEntity.get();
        Image image = mapFromCommandToDomain(imageEntityCurrenty,command);
        ImageEntity entity = ImageEntity.fromDomain(image);
        ImageEntity saved = repository.save(entity);
        return UpdateImageResult.builder().id(saved.getId()).build();
    }
    private Image mapFromCommandToDomain(ImageEntity imageEntityCurrenty,UpdateImageCommand command){
        return Image.builder()
                .id(new ImageId(command.id()))
                .bytes(new ImageBytes(imageEntityCurrenty.getImageBytes())) //No se actualizan los bytes de la imagen, se mantienen los mismos
                .mimeType(new ImageMimeType(imageEntityCurrenty.getImageMimeType())) //No se actualizan el mimeType de la imagen, se mantiene el mismo
                .order(new ImageOrder(command.order()))
                .primary(new IsPrimary(command.primary()))
                .productId(new ProductId(command.productId()))
                .build();
    }

}
