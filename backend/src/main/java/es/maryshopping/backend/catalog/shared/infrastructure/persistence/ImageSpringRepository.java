package es.maryshopping.backend.catalog.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageSpringRepository extends JpaRepository<ImageEntity, UUID> {
}
