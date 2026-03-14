package es.maryshopping.backend.catalog.usecases.delete_category.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.CategorySpringRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteCategoryService {
    private final CategorySpringRepository repository;

    public DeleteCategoryService(CategorySpringRepository repository) {
        this.repository = repository;
    }

    public DeleteCategoryResult proceed(DeleteCategoryCommand command) {
        repository.deleteById(command.id());
        return DeleteCategoryResult.builder().id(command.id()).build();
    }
}