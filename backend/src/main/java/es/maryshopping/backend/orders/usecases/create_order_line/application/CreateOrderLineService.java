package es.maryshopping.backend.orders.usecases.create_order_line.application;

import es.maryshopping.backend.orders.shared.domain.order.OrderId;
import es.maryshopping.backend.orders.shared.domain.order_line.*;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineSpringRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateOrderLineService {
    private final OrderLineSpringRepository repository;

    public CreateOrderLineService(OrderLineSpringRepository repository) {
        this.repository = repository;
    }

    public CreateOrderLineResult proceed(CreateOrderLineCommand command) {
        UUID orderLineId = UUID.randomUUID();
        OrderLine orderLine = mapFromCommandToDomain(orderLineId, command);
        OrderLineEntity entity = OrderLineEntity.fromDomain(orderLine);
        OrderLineEntity saved = repository.save(entity);
        return CreateOrderLineResult.builder().id(saved.getOrderLineId()).build();
    }

    private OrderLine mapFromCommandToDomain(UUID orderLineId, CreateOrderLineCommand command) {
       return OrderLine.builder()
               .id(new OrderLineId(orderLineId))
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
