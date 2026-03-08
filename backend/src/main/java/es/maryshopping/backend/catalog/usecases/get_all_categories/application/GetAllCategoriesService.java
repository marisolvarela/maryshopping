package es.maryshopping.backend.catalog.usecases.get_all_categories.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.CategoryEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.CategorySpringRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllCategoriesService {
    private final CategorySpringRepository repository;

    public GetAllCategoriesService(CategorySpringRepository repository) {
        this.repository = repository;
    }

    public List<CategoryResult> proceed(GetAllCategoriesQuery query) {
        List<CategoryEntity> categoryEntities = repository.findAll();
       return categoryEntities.stream()
               .map(this::fromEntityToResult)
               .toList();
    }
    private CategoryResult fromEntityToResult(CategoryEntity entity){
        return CategoryResult.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
