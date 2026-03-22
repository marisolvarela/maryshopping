package es.maryshopping.backend.orders.usecases.create_order.application;


import es.maryshopping.backend.orders.shared.domain.order.*;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderSpringRepository;
import es.maryshopping.backend.shared_kernel.domain.address.*;
import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateOrderService {
    private final OrderSpringRepository repository;

    public CreateOrderService(OrderSpringRepository repository) {
        this.repository = repository;
    }

    public CreateOrderResult proceed(CreateOrderCommand command){
        ensureCustomerHasNoUnconfirmedOrder(command.customerId());
        UUID orderId = UUID.randomUUID();
        Order order = fromCommandToDomain(orderId, command);
        OrderEntity entity = OrderEntity.fromDomain(order);
        OrderEntity saved = repository.save(entity);
        return CreateOrderResult.builder().id(saved.getId()).build();
    }
    private void ensureCustomerHasNoUnconfirmedOrder(UUID customerId) {
        boolean hasUnconfirmedOrder = repository.existsByCustomerIdAndStatus(
                customerId, StatusEnum.CREATED.name());
        if (hasUnconfirmedOrder) {
            throw new BusinessRuleException(
                    "Customer already has an unconfirmed order. Please confirm or delete it before creating a new one.");
        }
    }
    private Order fromCommandToDomain(UUID orderId,CreateOrderCommand command){
        Address shippingAddress = Address.builder()
                .street(new Street(command.shippingAddress().street()))
                .city(new City(command.shippingAddress().city()))
                .province(new Province(command.shippingAddress().province()))
                .postalCode(new PostalCode(command.shippingAddress().postalCode()))
                .country(new Country(command.shippingAddress().country()))
                .build();

        return Order.builder()
                .id(new OrderId(orderId))
                .stock(new Stock(10))
                .customerId(new CustomerId(command.customerId()))
                .status(new Status(StatusEnum.CREATED))
                .shippingAddress(new ShippingAddress(shippingAddress).value())
                .orderDate(new OrderDate(command.orderDate()))
                .build();
    }
}
