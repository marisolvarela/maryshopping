package es.maryshopping.backend.catalog.usecases.get_product.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.*;
import es.maryshopping.backend.shared_kernel.internal_api.product.application.GetProductQuery;
import es.maryshopping.backend.shared_kernel.internal_api.product.application.GetProductResult;
import es.maryshopping.backend.shared_kernel.internal_api.product.infrastructure.internal.GetProduct;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetProductService implements GetProduct {
    private final ProductSpringRepository productRepository;
    private final CategorySpringRepository categoryRepository;
    private final ProviderSpringRepository providerRepository;


    public GetProductService(ProductSpringRepository productRepository, CategorySpringRepository categoryRepository, ProviderSpringRepository providerRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.providerRepository = providerRepository;
    }

    public GetProductResult proceed(GetProductQuery query) {
        Optional<ProductEntity> productEntityOptional = productRepository.findById(query.id());
        ProductEntity productEntity = productEntityOptional.get();

        GetProductResult.CategoryResult categoryResult = buildCategoryResult(productEntity);
        GetProductResult.ProviderResult providerResult = buildProviderResult(productEntity);

        return GetProductResult.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .category(categoryResult)
                .description(productEntity.getDescription())
                .provider(providerResult)
                .providerReference(productEntity.getProductProviderRef())
                .price(productEntity.getPrice())
                .build();
    }

    private GetProductResult.CategoryResult buildCategoryResult(ProductEntity productEntity) {
        Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(productEntity.getCategoryId());
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        return GetProductResult.CategoryResult.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .build();
    }

    private GetProductResult.ProviderResult buildProviderResult(ProductEntity productEntity) {
        Optional<ProviderEntity> providerEntityOptional = providerRepository.findById(productEntity.getProviderId());
        ProviderEntity providerEntity = providerEntityOptional.get();
        return GetProductResult.ProviderResult.builder()
                .id(providerEntity.getId())
                .name(providerEntity.getName())
                .build();
    }
}
