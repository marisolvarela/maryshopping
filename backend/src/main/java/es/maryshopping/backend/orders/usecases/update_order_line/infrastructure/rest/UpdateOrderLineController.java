package es.maryshopping.backend.orders.usecases.update_order_line.infrastructure.rest;

import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderOwnershipResolver;
import es.maryshopping.backend.orders.usecases.update_order_line.application.UpdateOrderLineCommand;
import es.maryshopping.backend.orders.usecases.update_order_line.application.UpdateOrderLineResult;
import es.maryshopping.backend.orders.usecases.update_order_line.application.UpdateOrderLineService;
import es.maryshopping.backend.shared_kernel.security.CustomerOwnershipValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders/orderLine")
public class UpdateOrderLineController {
    private final UpdateOrderLineService updateOrderLineService;
    private final OrderOwnershipResolver orderOwnershipResolver;
    private final CustomerOwnershipValidator customerOwnershipValidator;

    public UpdateOrderLineController(UpdateOrderLineService updateOrderLineService,
                                     OrderOwnershipResolver orderOwnershipResolver,
                                     CustomerOwnershipValidator customerOwnershipValidator) {
        this.updateOrderLineService = updateOrderLineService;
        this.orderOwnershipResolver = orderOwnershipResolver;
        this.customerOwnershipValidator = customerOwnershipValidator;
    }

    @PutMapping("/{orderLineId}")
    public ResponseEntity<UpdateOrderLineResponse> proceed(@PathVariable UUID orderLineId, @RequestBody UpdateOrderLineRequest request){
        UUID resourceCustomerId = orderOwnershipResolver.resolveCustomerIdByOrderLineId(orderLineId);
        customerOwnershipValidator.validate(resourceCustomerId);
        UpdateOrderLineCommand command = mapFromRequestToCommand(orderLineId, request);
        UpdateOrderLineResult result = updateOrderLineService.proceed(command);
        UpdateOrderLineResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }
    private UpdateOrderLineCommand mapFromRequestToCommand(UUID orderLineId,UpdateOrderLineRequest request){
        return UpdateOrderLineCommand.builder()
                .id(orderLineId)
                .orderId(request.orderId())
                .categoryName(request.categoryName())
                .productId(request.productId())
                .productName(request.productName())
                .providerName(request.providerName())
                .productProviderReference(request.productProviderReference())
                .quantity(request.quantity())
                .unitPrice(request.unitPrice())
                .build();
    }
    private UpdateOrderLineResponse mapFromResultToResponse(UpdateOrderLineResult result){
        return UpdateOrderLineResponse.builder().orderLineId(result.orderLineId()).build();
    }
}
