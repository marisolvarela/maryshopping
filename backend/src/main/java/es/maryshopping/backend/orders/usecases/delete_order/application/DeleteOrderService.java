package es.maryshopping.backend.orders.usecases.delete_order.application;

import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderSpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeleteOrderService {
    private final OrderSpringRepository repository;

    public DeleteOrderService(OrderSpringRepository repository) {
        this.repository = repository;
    }

    public DeleteOrderResult proceed(DeleteOrderCommand command){
        Optional<OrderEntity> optionalOrderEntity = repository.findById(command.id());
        if (optionalOrderEntity.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }
        OrderEntity entity = optionalOrderEntity.get();
        repository.delete(entity);
        return DeleteOrderResult.builder().id(entity.getId()).build();
    }
}
