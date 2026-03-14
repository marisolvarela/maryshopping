package es.maryshopping.backend.catalog.usecases.delete_product.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProductSpringRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteProductService {
    private final ProductSpringRepository repository;

    public DeleteProductService(ProductSpringRepository repository) {
        this.repository = repository;
    }

    public DeleteProductResult proceed(DeleteProductCommand command) {
        repository.deleteById(command.id());
        return DeleteProductResult.builder().id(command.id()).build();
    }
}
