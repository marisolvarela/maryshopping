package es.maryshopping.backend.orders.usecases.update_order.infrastructure.rest;

import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderOwnershipResolver;
import es.maryshopping.backend.orders.usecases.update_order.application.UpdateOrderCommand;
import es.maryshopping.backend.orders.usecases.update_order.application.UpdateOrderResult;
import es.maryshopping.backend.orders.usecases.update_order.application.UpdateOrderService;
import es.maryshopping.backend.shared_kernel.security.CustomerOwnershipValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders/order")
public class UpdateOrderController {
    private final UpdateOrderService updateOrderService;
    private final OrderOwnershipResolver orderOwnershipResolver;
    private final CustomerOwnershipValidator customerOwnershipValidator;

    public UpdateOrderController(UpdateOrderService updateOrderService,
                                 OrderOwnershipResolver orderOwnershipResolver,
                                 CustomerOwnershipValidator customerOwnershipValidator) {
        this.updateOrderService = updateOrderService;
        this.orderOwnershipResolver = orderOwnershipResolver;
        this.customerOwnershipValidator = customerOwnershipValidator;
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<UpdateOrderResponse> proceed(@PathVariable UUID orderId,@RequestBody UpdateOrderRequest request){
        UUID resourceCustomerId = orderOwnershipResolver.resolveCustomerIdByOrderId(orderId);
        customerOwnershipValidator.validate(resourceCustomerId);
        UpdateOrderCommand command = mapFromRequestToCommand(orderId, request);
        UpdateOrderResult result = updateOrderService.proceed(command);
        UpdateOrderResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }
    private UpdateOrderCommand mapFromRequestToCommand(UUID orderId, UpdateOrderRequest request){
        UpdateOrderCommand.AddressCommand shippingAddressCommand = UpdateOrderCommand.AddressCommand.builder()
                .city(request.shippingAddress().city())
                .country(request.shippingAddress().country())
                .postalCode(request.shippingAddress().postalCode())
                .street(request.shippingAddress().street())
                .province(request.shippingAddress().province())
                .build();
        return UpdateOrderCommand.builder()
                .id(orderId)
                .orderDate(request.orderDate())
                .customerId(request.customerId())
                .shippingAddress(shippingAddressCommand)
                .build();
    }
    private UpdateOrderResponse mapFromResultToResponse(UpdateOrderResult result){
        return UpdateOrderResponse.builder().id(result.id()).build();
    }
}
