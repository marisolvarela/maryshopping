package es.maryshopping.backend.orders.usecases.delete_order_line.infrastructure.rest;

import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderOwnershipResolver;
import es.maryshopping.backend.orders.usecases.delete_order_line.application.DeleteOrderLineCommand;
import es.maryshopping.backend.orders.usecases.delete_order_line.application.DeleteOrderLineResult;
import es.maryshopping.backend.orders.usecases.delete_order_line.application.DeleteOrderLineService;
import es.maryshopping.backend.shared_kernel.security.CustomerOwnershipValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders/orderLine")
public class DeleteOderLineController {
    private final DeleteOrderLineService orderLineService;
    private final OrderOwnershipResolver orderOwnershipResolver;
    private final CustomerOwnershipValidator customerOwnershipValidator;

    public DeleteOderLineController(DeleteOrderLineService orderLineService,
                                    OrderOwnershipResolver orderOwnershipResolver,
                                    CustomerOwnershipValidator customerOwnershipValidator) {
        this.orderLineService = orderLineService;
        this.orderOwnershipResolver = orderOwnershipResolver;
        this.customerOwnershipValidator = customerOwnershipValidator;
    }

    @DeleteMapping("/{orderLineId}")
    public ResponseEntity<DeleteOrderLineResponse> proceed(@PathVariable UUID orderLineId){
        UUID resourceCustomerId = orderOwnershipResolver.resolveCustomerIdByOrderLineId(orderLineId);
        customerOwnershipValidator.validate(resourceCustomerId);
        DeleteOrderLineCommand command = mapFromPathToCommand(orderLineId);
        DeleteOrderLineResult result = orderLineService.proceed(command);
        DeleteOrderLineResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);

    }
    private DeleteOrderLineCommand mapFromPathToCommand(UUID orderLineId){
        return DeleteOrderLineCommand.builder()
                .orderLineId(orderLineId)
                .build();
    }
    private DeleteOrderLineResponse mapFromResultToResponse(DeleteOrderLineResult result){
        return DeleteOrderLineResponse.builder().id(result.id()).build();
    }
}
