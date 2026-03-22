package es.maryshopping.backend.orders.usecases.delete_order.infrastructure.rest;

import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderOwnershipResolver;
import es.maryshopping.backend.orders.usecases.delete_order.application.DeleteOrderCommand;
import es.maryshopping.backend.orders.usecases.delete_order.application.DeleteOrderResult;
import es.maryshopping.backend.orders.usecases.delete_order.application.DeleteOrderService;
import es.maryshopping.backend.shared_kernel.security.CustomerOwnershipValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders/order")
public class DeleteOrderController {
    private final DeleteOrderService deleteOrderService;
    private final OrderOwnershipResolver orderOwnershipResolver;
    private final CustomerOwnershipValidator customerOwnershipValidator;

    public DeleteOrderController(DeleteOrderService deleteOrderService,
                                 OrderOwnershipResolver orderOwnershipResolver,
                                 CustomerOwnershipValidator customerOwnershipValidator) {
        this.deleteOrderService = deleteOrderService;
        this.orderOwnershipResolver = orderOwnershipResolver;
        this.customerOwnershipValidator = customerOwnershipValidator;
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<DeleteOrderResponse> proceed(@PathVariable UUID orderId){
        UUID resourceCustomerId = orderOwnershipResolver.resolveCustomerIdByOrderId(orderId);
        customerOwnershipValidator.validate(resourceCustomerId);
        DeleteOrderCommand command = mapFromPathToCommand(orderId);
        DeleteOrderResult result = deleteOrderService.proceed(command);
        DeleteOrderResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }
    private DeleteOrderCommand mapFromPathToCommand(UUID orderId){
        return DeleteOrderCommand.builder()
                .id(orderId)
                .build();
    }
    private DeleteOrderResponse mapFromResultToResponse(DeleteOrderResult result){
        return DeleteOrderResponse.builder().id(result.id()).build();
    }
}
