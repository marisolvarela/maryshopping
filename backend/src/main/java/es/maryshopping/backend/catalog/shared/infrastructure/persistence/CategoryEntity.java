package es.maryshopping.backend.catalog.shared.infrastructure.persistence;

import es.maryshopping.backend.catalog.shared.domain.category.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="category", schema = "catalog")
public class CategoryEntity {
    @Id
    UUID id;
    String name;

    public static CategoryEntity fromDomain(Category category) {
        return new CategoryEntity(
                category.id().value(),
                category.name().value()
        );
    }
}
