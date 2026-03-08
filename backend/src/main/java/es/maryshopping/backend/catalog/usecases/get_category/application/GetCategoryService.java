package es.maryshopping.backend.catalog.usecases.get_category.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.CategoryEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.CategorySpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetCategoryService {
    private final CategorySpringRepository repository;

    public GetCategoryService(CategorySpringRepository repository) {
        this.repository = repository;
    }

    public GetCategoryResult proceed(GetCategoryQuery query){
        Optional<CategoryEntity> entityOptional = repository.findById(query.id());
        if (entityOptional.isEmpty()){
            throw new EntityNotFoundException("Category not found");
        }
        //obtenemos el objeto de persistencia(entity) a partir del id de la categoría
        CategoryEntity entity = entityOptional.get();
        //Devolvemos el resultado con los datos de la categoría
        return fromEntityToResult(entity);
    }

    private GetCategoryResult fromEntityToResult(CategoryEntity entity) {
        return GetCategoryResult.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
