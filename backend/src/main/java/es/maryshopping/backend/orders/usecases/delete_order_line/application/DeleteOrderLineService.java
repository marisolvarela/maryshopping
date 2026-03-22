package es.maryshopping.backend.orders.usecases.delete_order_line.application;

import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineSpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeleteOrderLineService {
    private final OrderLineSpringRepository repository;

    public DeleteOrderLineService(OrderLineSpringRepository repository) {
        this.repository = repository;
    }

    public DeleteOrderLineResult proceed(DeleteOrderLineCommand command){
        Optional<OrderLineEntity> optionalOrderLineEntity = repository.findById(command.orderLineId());
        if (optionalOrderLineEntity.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }
        OrderLineEntity entity = optionalOrderLineEntity.get();
        repository.delete(entity);
        return DeleteOrderLineResult.builder().id(entity.getOrderLineId()).build();
    }

}
