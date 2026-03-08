package es.maryshopping.backend.catalog.usecases.get_category.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.get_category.application.GetCategoryQuery;
import es.maryshopping.backend.catalog.usecases.get_category.application.GetCategoryResult;
import es.maryshopping.backend.catalog.usecases.get_category.application.GetCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/categories")
public class GetCategoryController {
    private final GetCategoryService getCategoryService;

    public GetCategoryController(GetCategoryService getCategoryService) {
        this.getCategoryService = getCategoryService;
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<GetCategoryResponse> proceed(@PathVariable UUID categoryId) {
        GetCategoryQuery query = mapFromPathVariableToQuery(categoryId);
        GetCategoryResult result = getCategoryService.proceed(query);
        GetCategoryResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);

    }

    private GetCategoryQuery mapFromPathVariableToQuery(UUID categoryId) {
        return new GetCategoryQuery(categoryId);
    }

    private GetCategoryResponse mapFromResultToResponse(GetCategoryResult result) {
        return GetCategoryResponse.builder().id(result.id()).name(result.name()).build();
    }
}
