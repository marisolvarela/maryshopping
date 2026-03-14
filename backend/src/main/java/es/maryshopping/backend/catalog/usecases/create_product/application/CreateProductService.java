package es.maryshopping.backend.catalog.usecases.create_product.application;

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
import java.util.UUID;

@Service
public class CreateProductService {
    private final ProductSpringRepository productRepository;
    private final CategorySpringRepository categoryRepository;
    private final ProviderSpringRepository providerRepository;

    public CreateProductService(ProductSpringRepository productRepository, CategorySpringRepository categoryRepository, ProviderSpringRepository providerRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.providerRepository = providerRepository;
    }

    public CreateProductResult proceed(CreateProductCommand command) {
        UUID productId = UUID.randomUUID();
        //creamos el objeto de dominio a partir del comando
        Product product = fromCommandToDomain(productId, command);
        //Creamos la entidad a partir del objeto de dominio
        ProductEntity entity = ProductEntity.fromDomain(product);
        //Guardamos la entidad en la base de datos
        ProductEntity saved = productRepository.save(entity);
        return CreateProductResult.builder().productId(saved.getId()).build();
    }

    private Product fromCommandToDomain(UUID productId, CreateProductCommand command) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(command.categoryId());
        if (categoryOptional.isEmpty()) {
            throw new EntityNotFoundException("category not found");
        }
        CategoryEntity categoryEntity = categoryOptional.get();

        Optional<ProviderEntity> providerOptional = providerRepository.findById(command.providerId());
        if (providerOptional.isEmpty()) {
            throw new EntityNotFoundException("Provider not found");
        }
        ProviderEntity providerEntity = providerOptional.get();
        //Creamos el objeto de dominio a partir de la entidad
        Category category = Category.builder()
                .id(new CategoryId(command.categoryId()))
                .name(new CategoryName(categoryEntity.getName()))
                .build();
        //Creamos el objeto de dominio a partir de la entidad
        Provider provider = Provider.builder()
                .id(new ProviderId(command.providerId()))
                .name(new ProviderName(providerEntity.getName()))
                .build();
        //Creamos el objeto de dominio a partir del comando y de las entidades
        return Product.builder()
                .id(new ProductId(productId))
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
