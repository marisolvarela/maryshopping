package es.maryshopping.backend.orders.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderLineSpringRepository extends JpaRepository<OrderLineEntity, UUID> {
    //si ponemos bien el nombre que viene en la OrderLineEntity(orderId), spring lo interpreta y hace la consulta sin necesidad de escribirla.
    List<OrderLineEntity> getAllByOrderId(UUID orderId);
}
