package es.maryshopping.backend.customers.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerSpringRepository extends JpaRepository<CustomerEntity, UUID> {

    Optional<CustomerEntity> findByDni(String dni);

    Optional<CustomerEntity> findByEmailAddress(String dni);

}
