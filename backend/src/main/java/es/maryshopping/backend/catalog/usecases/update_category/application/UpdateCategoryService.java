package es.maryshopping.backend.catalog.usecases.update_category.application;

import es.maryshopping.backend.catalog.shared.domain.category.Category;
import es.maryshopping.backend.catalog.shared.domain.category.CategoryId;
import es.maryshopping.backend.catalog.shared.domain.category.CategoryName;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.CategoryEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.CategorySpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateCategoryService {
    private final CategorySpringRepository repository;

    public UpdateCategoryService(CategorySpringRepository repository) {
        this.repository = repository;
    }

    public UpdateCategoryResult proceed(UpdateCategoryCommand command) {
        Optional<CategoryEntity> categoryEntityOptional = repository.findById(command.id());
        if (categoryEntityOptional.isEmpty()) {
            throw  new EntityNotFoundException("Category not found");
        }
        //Creamos el objeto de dominio a partir del comando
        Category category = fromCommandToDomain(command);
        //Creamos el objeto de persistencia(entity) a partir del objeto de dominio
        CategoryEntity entity = CategoryEntity.fromDomain(category);
        //Guardamos el objeto en la base de datos
        CategoryEntity saved = repository.save(entity);
        //Devolvemos el resultado con el id de la categoría creada
        return UpdateCategoryResult.builder().id(saved.getId()).build();
    }
    private Category fromCommandToDomain(UpdateCategoryCommand command){
        return Category.builder()
                .id(new CategoryId(command.id()))
                .name(new CategoryName(command.name()))
                .build();
    }
}
