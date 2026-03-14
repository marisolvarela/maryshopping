package es.maryshopping.backend.catalog.usecases.get_products_in_category.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.*;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetProductsInCategoryService {
    private final ProductSpringRepository productRepository;
    private final CategorySpringRepository categoryRepository;
    private final ProviderSpringRepository providerRepository;

    public GetProductsInCategoryService(ProductSpringRepository productRepository,
                                        CategorySpringRepository categoryRepository,
                                        ProviderSpringRepository providerRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.providerRepository = providerRepository;
    }

    public List<ProductInCategoryResult> proceed(GetProductsInCategoryQuery query) {
        checkCategoryExists(query.categoryId());
        List<ProductEntity> productEntities = productRepository.findByCategoryId(query.categoryId());
        return productEntities.stream()
                .map(this::fromEntityToResult)
                .toList();
    }

    private void checkCategoryExists(UUID categoryId) {
        Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(categoryId);
        if (categoryEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("Category not found");
        }
    }

    private ProductInCategoryResult fromEntityToResult(ProductEntity productEntity) {
        ProductInCategoryResult.CategoryResult categoryResult = buildCategoryResult(productEntity);
        ProductInCategoryResult.ProviderResult providerResult = buildProviderResult(productEntity);
        return ProductInCategoryResult.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .category(categoryResult)
                .description(productEntity.getDescription())
                .provider(providerResult)
                .providerReference(productEntity.getProductProviderRef())
                .price(productEntity.getPrice())
                .build();
    }

    private ProductInCategoryResult.CategoryResult buildCategoryResult(ProductEntity productEntity) {
        Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(productEntity.getCategoryId());
        if (categoryEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("Category not found");
        }
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        return ProductInCategoryResult.CategoryResult.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .build();
    }

    private ProductInCategoryResult.ProviderResult buildProviderResult(ProductEntity productEntity) {
        Optional<ProviderEntity> providerEntityOptional = providerRepository.findById(productEntity.getProviderId());
        if (providerEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("Provider not found");
        }
        ProviderEntity providerEntity = providerEntityOptional.get();
        return ProductInCategoryResult.ProviderResult.builder()
                .id(providerEntity.getId())
                .name(providerEntity.getName())
                .build();
    }
}

