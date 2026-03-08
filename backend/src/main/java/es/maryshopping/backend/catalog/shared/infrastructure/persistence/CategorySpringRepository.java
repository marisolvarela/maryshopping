package es.maryshopping.backend.catalog.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategorySpringRepository extends JpaRepository<CategoryEntity, UUID> {

    Optional<CategoryEntity>findByName(String name);
}
