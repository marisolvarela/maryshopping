package es.maryshopping.backend.catalog.shared.infrastructure.persistence;

import es.maryshopping.backend.catalog.shared.domain.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="product", schema="catalog")
public class ProductEntity {
    @Id
    UUID id;
    String name;
    UUID categoryId;
    String description;
    UUID providerId;
    String productProviderRef;
    BigDecimal price;

    public static ProductEntity fromDomain(Product product) {
        return new ProductEntity(
                product.id().value(),
                product.name().value(),
                product.category().id().value(),
                product.description().value(),
                product.provider().id().value(),
                product.productProviderReference().value(),
                product.price().value()
        );
    }
}
