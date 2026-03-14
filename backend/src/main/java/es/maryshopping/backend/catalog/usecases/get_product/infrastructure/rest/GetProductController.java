package es.maryshopping.backend.catalog.usecases.get_product.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.get_product.application.GetProductService;
import es.maryshopping.backend.shared_kernel.internal_api.product.application.GetProductQuery;
import es.maryshopping.backend.shared_kernel.internal_api.product.application.GetProductResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/products")
public class GetProductController {
    private final GetProductService getProductService;

    public GetProductController(GetProductService getProductService) {
        this.getProductService = getProductService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<GetProductResponse> proceed(@PathVariable UUID productId) {
        GetProductQuery query = mapFromPathToQuery(productId);
        GetProductResult result = getProductService.proceed(query);
        GetProductResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);

    }

    private GetProductQuery mapFromPathToQuery(UUID productId) {
        return new GetProductQuery(productId);
    }

    private GetProductResponse mapFromResultToResponse(GetProductResult result) {
        return GetProductResponse.builder()
                .id(result.id())
                .name(result.name())
                .category(mapFromCategoryResultToCategoryResponse(result.category()))
                .description(result.description())
                .provider(mapFromProviderResultToProviderResponse(result.provider()))
                .providerReference(result.providerReference())
                .price(result.price())
                .build();
    }

    private GetProductResponse.CategoryResponse mapFromCategoryResultToCategoryResponse(
            GetProductResult.CategoryResult categoryResult) {
        return GetProductResponse.CategoryResponse.builder()
                .id(categoryResult.id())
                .name(categoryResult.name())
                .build();
    }

    private GetProductResponse.ProviderResponse mapFromProviderResultToProviderResponse(
            GetProductResult.ProviderResult providerResult) {
        return GetProductResponse.ProviderResponse.builder()
                .id(providerResult.id())
                .name(providerResult.name())
                .build();
    }
}
