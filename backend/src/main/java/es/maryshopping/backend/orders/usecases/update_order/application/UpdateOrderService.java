package es.maryshopping.backend.orders.usecases.update_order.application;

import es.maryshopping.backend.orders.shared.domain.order.*;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderSpringRepository;
import es.maryshopping.backend.shared_kernel.domain.address.*;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateOrderService {
    private final OrderSpringRepository repository;

    public UpdateOrderService(OrderSpringRepository repository) {
        this.repository = repository;
    }

    public UpdateOrderResult proceed(UpdateOrderCommand command) {
        Optional<OrderEntity> optionalOrderEntity = repository.findById(command.id());
        if (optionalOrderEntity.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }
        //Busco en la base de datos la entidad actual para mantener el status y el stock, ya que no se actualizan en este caso
        OrderEntity currentEntity = optionalOrderEntity.get();
        //Con lo que me llega en el comando y lo que tengo en la base de datos(el estado, que no me viene en el comando) creo un nuevo Order actualizado
        Order orderUpdated = mapFromCommandToDomain(command,currentEntity);
        //Creo una entity nueva para poder guardarla
        OrderEntity newEntity = OrderEntity.fromDomain(orderUpdated);
        //Guardo la nueva entity
        OrderEntity saved = repository.save(newEntity);
        return UpdateOrderResult.builder().id(saved.getId()).build();
    }

    private Order mapFromCommandToDomain(UpdateOrderCommand command,OrderEntity currentEntity){
        Address address = Address.builder()
                .city(new City(command.shippingAddress().city()))
                .country(new Country(command.shippingAddress().country()))
                .postalCode(new PostalCode(command.shippingAddress().postalCode()))
                .province(new Province(command.shippingAddress().province()))
                .street(new Street(command.shippingAddress().street()))
                .build();

        return Order.builder()
                .id(new OrderId(command.id()))
                .orderDate(new OrderDate(command.orderDate()))
                .customerId(new CustomerId(command.customerId()))
                .shippingAddress(address)
                .stock(new Stock(35))
                .status(new Status(StatusEnum.valueOf(currentEntity.getStatus())))
                .build();
    }
}
