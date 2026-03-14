package es.maryshopping.backend.catalog.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductSpringRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findByCategoryId(UUID categoryId);
}
