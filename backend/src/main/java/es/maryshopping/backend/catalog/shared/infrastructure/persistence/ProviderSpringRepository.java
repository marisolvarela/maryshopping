package es.maryshopping.backend.catalog.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProviderSpringRepository extends JpaRepository<ProviderEntity, UUID> {
    Optional<ProviderEntity> findByName(String name);
}
