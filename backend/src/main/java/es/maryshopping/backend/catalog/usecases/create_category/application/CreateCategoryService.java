package es.maryshopping.backend.catalog.usecases.create_category.application;

import es.maryshopping.backend.catalog.shared.domain.category.Category;
import es.maryshopping.backend.catalog.shared.domain.category.CategoryId;
import es.maryshopping.backend.catalog.shared.domain.category.CategoryName;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.CategoryEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.CategorySpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.DuplicatedEntityException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CreateCategoryService {

    private final CategorySpringRepository repository;

    public CreateCategoryService(CategorySpringRepository repository) {
        this.repository = repository;
    }

    /*
    Para guardar los datos en la tabla
     */
    public CreateCategoryResult proceed(CreateCategoryCommand command) {
        Optional<CategoryEntity> optionalByName = repository.findByName(command.name());
        if (optionalByName.isPresent()) {
            throw  new DuplicatedEntityException("A category with that name already exits");
        }
        UUID randomUUID = UUID.randomUUID();
        //Creamos el objeto de dominio a partir del comando
        Category category = fromCommandToDomain(randomUUID, command);
        //Creamos el objeto de persistencia(entity) a partir del objeto de dominio
        CategoryEntity entity = CategoryEntity.fromDomain(category);
        //Guardamos el objeto en la base de datos
        CategoryEntity saved = repository.save(entity);
        //Devolvemos el resultado con el id de la categoría creada
        return CreateCategoryResult.builder().id(saved.getId()).build();
    }

    private Category fromCommandToDomain(UUID randomUUID, CreateCategoryCommand command) {
        return Category.builder()
                .id(new CategoryId(randomUUID))
                .name(new CategoryName(command.name()))
                .build();
    }
}
