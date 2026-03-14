package es.maryshopping.backend.catalog.usecases.update_product.application;

import es.maryshopping.backend.catalog.shared.domain.category.Category;
import es.maryshopping.backend.catalog.shared.domain.category.CategoryId;
import es.maryshopping.backend.catalog.shared.domain.category.CategoryName;
import es.maryshopping.backend.catalog.shared.domain.product.*;
import es.maryshopping.backend.catalog.shared.domain.provider.Provider;
import es.maryshopping.backend.catalog.shared.domain.provider.ProviderId;
import es.maryshopping.backend.catalog.shared.domain.provider.ProviderName;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.*;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UpdateProductService {
    private final ProductSpringRepository productRepository;
    private final CategorySpringRepository categoryRepository;
    private final ProviderSpringRepository providerRepository;

    public UpdateProductService(ProductSpringRepository productRepository, CategorySpringRepository categoryRepository, ProviderSpringRepository providerRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.providerRepository = providerRepository;
    }

    public UpdateProductResult proceed(UpdateProductCommand command) {
        Optional<ProductEntity> optionalProduct = productRepository.findById(command.id());
        if (optionalProduct.isEmpty()) {
            throw new EntityNotFoundException("Product not found");
        }

        Product product = fromCommandToDomain(command);
        ProductEntity entity = ProductEntity.fromDomain(product);
        ProductEntity saved = productRepository.save(entity);

        return UpdateProductResult.builder().id(saved.getId()).build();
    }

    private Product fromCommandToDomain(UpdateProductCommand command) {
        Optional<CategoryEntity> optionalCategory = categoryRepository.findById(command.categoryId());
        if (optionalCategory.isEmpty()) {
            throw new EntityNotFoundException("Category not found");
        }
        CategoryEntity categoryEntity = optionalCategory.get();

        Optional<ProviderEntity> optionalProvider = providerRepository.findById(command.provider());
        if (optionalProvider.isEmpty()) {
            throw new EntityNotFoundException("Provider not found");
        }
        ProviderEntity providerEntity = optionalProvider.get();
//creamos el objeto de dominio para comprobar que se cumplen las reglas de negocio
        Category category = Category.builder()
                .id(new CategoryId(command.categoryId()))
                .name(new CategoryName(categoryEntity.getName()))
                .build();
        Provider provider = Provider.builder()
                .id(new ProviderId(command.provider()))
                .name(new ProviderName(providerEntity.getName()))
                .build();

        return Product.builder()
                .id(new ProductId(command.id()))
                .name(new ProductName(command.name()))
                .category(category)
                .description(new ProductDescription(command.description()))
                .provider(provider)
                .productProviderReference(new ProductProviderReference(command.providerReference()))
                .price(new Price(command.price()))
                .images(List.of())
                .build();
    }
}

