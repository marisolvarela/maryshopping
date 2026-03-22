package es.maryshopping.backend.orders.usecases.create_order.infrastructure.rest;

import es.maryshopping.backend.orders.usecases.create_order.application.CreateOrderCommand;
import es.maryshopping.backend.orders.usecases.create_order.application.CreateOrderResult;
import es.maryshopping.backend.orders.usecases.create_order.application.CreateOrderService;
import es.maryshopping.backend.shared_kernel.security.CustomerOwnershipValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders/order")
public class CreateOrderController {
    private final CreateOrderService createOrderService;
    private final CustomerOwnershipValidator customerOwnershipValidator;

    public CreateOrderController(CreateOrderService createOrderService,
                                 CustomerOwnershipValidator customerOwnershipValidator) {
        this.createOrderService = createOrderService;
        this.customerOwnershipValidator = customerOwnershipValidator;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> proceed(@RequestBody CreateOrderRequest request){
        customerOwnershipValidator.validate(request.customerId());
        CreateOrderCommand command = mapFromRequestToCommand(request);
        CreateOrderResult result = createOrderService.proceed(command);
        CreateOrderResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }
    private CreateOrderCommand mapFromRequestToCommand(CreateOrderRequest request){
        CreateOrderCommand.AddressCommand shippingAddress = CreateOrderCommand.AddressCommand.builder()
                .street(request.shippingAddress().street())
                .city(request.shippingAddress().city())
                .province(request.shippingAddress().province())
                .postalCode(request.shippingAddress().postalCode())
                .country(request.shippingAddress().country())
                .build();

        return CreateOrderCommand.builder()
                .customerId(request.customerId())
                .orderDate(request.orderDate())
                .shippingAddress(shippingAddress)
                .build();
    }
    private CreateOrderResponse mapFromResultToResponse(CreateOrderResult result){
        return CreateOrderResponse.builder().id(result.id()).build();
    }
}
