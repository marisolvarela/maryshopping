package es.maryshopping.backend.orders.shared.infrastructure.persistence;

import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Resuelve el customerId propietario de un pedido o de una línea de pedido.
 *
 * Los controladores de orders no reciben customerId en la URL (reciben orderId u orderLineId).
 * Este servicio consulta la base de datos para averiguar a qué customer pertenece el recurso,
 * de modo que luego se pueda invocar a CustomerOwnershipValidator.validate(customerId).
 */
@Service
public class OrderOwnershipResolver {

    private final OrderSpringRepository orderRepository;
    private final OrderLineSpringRepository orderLineRepository;

    public OrderOwnershipResolver(OrderSpringRepository orderRepository,
                                  OrderLineSpringRepository orderLineRepository) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
    }

    /**
     * Dado un orderId, devuelve el customerId propietario del pedido.
     *
     * @param orderId UUID del pedido
     * @return UUID del customer propietario
     * @throws EntityNotFoundException si el pedido no existe
     */
    public UUID resolveCustomerIdByOrderId(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        return order.getCustomerId();
    }

    /**
     * Dado un orderLineId, devuelve el customerId propietario de la línea de pedido.
     *
     * Cadena de resolución: orderLineId → orderId → customerId.
     *
     * @param orderLineId UUID de la línea de pedido
     * @return UUID del customer propietario
     * @throws EntityNotFoundException si la línea de pedido o el pedido no existen
     */
    public UUID resolveCustomerIdByOrderLineId(UUID orderLineId) {
        OrderLineEntity orderLine = orderLineRepository.findById(orderLineId)
                .orElseThrow(() -> new EntityNotFoundException("Order line not found: " + orderLineId));
        return resolveCustomerIdByOrderId(orderLine.getOrderId());
    }
}

