package es.maryshopping.backend.orders.usecases.update_order_line.application;

import es.maryshopping.backend.orders.shared.domain.order.OrderId;
import es.maryshopping.backend.orders.shared.domain.order_line.*;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineSpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateOrderLineService {
    private final OrderLineSpringRepository repository;

    public UpdateOrderLineService(OrderLineSpringRepository repository) {
        this.repository = repository;
    }

    public UpdateOrderLineResult proceed(UpdateOrderLineCommand command){
        Optional<OrderLineEntity> optionalOrderLineEntity = repository.findById(command.id());
        if (optionalOrderLineEntity.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }
        OrderLine orderLine = mapFromCommandToDomain(command);
        OrderLineEntity entity = OrderLineEntity.fromDomain(orderLine);
        OrderLineEntity saved = repository.save(entity);
        return UpdateOrderLineResult.builder().orderLineId(saved.getOrderLineId()).build();
    }
    private OrderLine mapFromCommandToDomain(UpdateOrderLineCommand command){
        return OrderLine.builder()
                .id(new OrderLineId(command.id()))
                .orderId(new OrderId(command.orderId()))
                .categoryName(new CategoryName(command.categoryName()))
                .productId(new ProductId(command.productId()))
                .productName(new ProductName(command.productName()))
                .providerName(new ProviderName(command.providerName()))
                .productProviderReference(new ProductProviderReference(command.productProviderReference()))
                .quantity(new Quantity(command.quantity()))
                .unitPrice(new UnitPrice(command.unitPrice()))
                .build();
    }
}
