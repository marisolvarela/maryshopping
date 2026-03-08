package es.maryshopping.backend.catalog.usecases.get_all_categories.infrastrucutre.rest;

import es.maryshopping.backend.catalog.usecases.get_all_categories.application.CategoryResult;
import es.maryshopping.backend.catalog.usecases.get_all_categories.application.GetAllCategoriesQuery;
import es.maryshopping.backend.catalog.usecases.get_all_categories.application.GetAllCategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog/categories")
public class GetAllCategoriesController {
    private final GetAllCategoriesService GetAllCategoriesService;

    public GetAllCategoriesController(GetAllCategoriesService getAllCategoriesService) {
        this.GetAllCategoriesService = getAllCategoriesService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> proceed() {
        GetAllCategoriesQuery query = createQuery();
        List<CategoryResult> categoryResults = GetAllCategoriesService.proceed(query);
        List<CategoryResponse> categoryResponses = categoryResults.stream()
                .map(this::mapFromResultToResponse)
                .toList();
        return ResponseEntity.status(200).body(categoryResponses);
    }

    private GetAllCategoriesQuery createQuery() {
        return new GetAllCategoriesQuery();
    }

    private CategoryResponse mapFromResultToResponse(CategoryResult result) {
        return CategoryResponse.builder().id(result.id()).name(result.name()).build();
    }
}
