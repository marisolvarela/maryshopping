package es.maryshopping.backend.catalog.usecases.images.delete_image.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ImageSpringRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteImageService {
    private final ImageSpringRepository repository;

    public DeleteImageService(ImageSpringRepository repository) {
        this.repository = repository;
    }
    public DeleteImageResult proceed(DeleteImageCommand command){
        repository.deleteById(command.id());
        return DeleteImageResult.builder().id(command.id()).build();
    }
}
