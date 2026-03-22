package es.maryshopping.backend.orders.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderSpringRepository extends JpaRepository<OrderEntity, UUID> {
    boolean existsByCustomerIdAndStatus(UUID customerId, String status);
}
