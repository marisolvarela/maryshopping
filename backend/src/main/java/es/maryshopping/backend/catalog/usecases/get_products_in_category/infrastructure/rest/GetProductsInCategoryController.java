package es.maryshopping.backend.catalog.usecases.get_products_in_category.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.get_products_in_category.application.GetProductsInCategoryQuery;
import es.maryshopping.backend.catalog.usecases.get_products_in_category.application.GetProductsInCategoryService;
import es.maryshopping.backend.catalog.usecases.get_products_in_category.application.ProductInCategoryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/catalog/categories")
public class GetProductsInCategoryController {
    private final GetProductsInCategoryService getProductsInCategoryService;

    public GetProductsInCategoryController(GetProductsInCategoryService getProductsInCategoryService) {
        this.getProductsInCategoryService = getProductsInCategoryService;
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<List<ProductInCategoryResponse>> proceed(@PathVariable UUID categoryId) {
        GetProductsInCategoryQuery query = mapFromPathToQuery(categoryId);
        List<ProductInCategoryResult> results = getProductsInCategoryService.proceed(query);
        List<ProductInCategoryResponse> responses = results.stream()
                .map(this::mapFromResultToResponse)
                .toList();
        return ResponseEntity.status(200).body(responses);
    }

    private GetProductsInCategoryQuery mapFromPathToQuery(UUID categoryId) {
        return new GetProductsInCategoryQuery(categoryId);
    }

    private ProductInCategoryResponse mapFromResultToResponse(ProductInCategoryResult result) {
        return ProductInCategoryResponse.builder()
                .id(result.id())
                .name(result.name())
                .category(mapFromCategoryResultToCategoryResponse(result.category()))
                .description(result.description())
                .provider(mapFromProviderResultToProviderResponse(result.provider()))
                .providerReference(result.providerReference())
                .price(result.price())
                .build();
    }

    private ProductInCategoryResponse.CategoryResponse mapFromCategoryResultToCategoryResponse(
            ProductInCategoryResult.CategoryResult categoryResult) {
        return ProductInCategoryResponse.CategoryResponse.builder()
                .id(categoryResult.id())
                .name(categoryResult.name())
                .build();
    }

    private ProductInCategoryResponse.ProviderResponse mapFromProviderResultToProviderResponse(
            ProductInCategoryResult.ProviderResult providerResult) {
        return ProductInCategoryResponse.ProviderResponse.builder()
                .id(providerResult.id())
                .name(providerResult.name())
                .build();
    }
}

