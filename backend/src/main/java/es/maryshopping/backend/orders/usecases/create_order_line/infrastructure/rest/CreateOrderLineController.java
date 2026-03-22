package es.maryshopping.backend.orders.usecases.create_order_line.infrastructure.rest;

import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderOwnershipResolver;
import es.maryshopping.backend.orders.usecases.create_order_line.application.CreateOrderLineCommand;
import es.maryshopping.backend.orders.usecases.create_order_line.application.CreateOrderLineResult;
import es.maryshopping.backend.orders.usecases.create_order_line.application.CreateOrderLineService;
import es.maryshopping.backend.shared_kernel.security.CustomerOwnershipValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders/orderLine")
public class CreateOrderLineController {
    private final CreateOrderLineService orderLineService;
    private final OrderOwnershipResolver orderOwnershipResolver;
    private final CustomerOwnershipValidator customerOwnershipValidator;

    public CreateOrderLineController(CreateOrderLineService orderLineService,
                                     OrderOwnershipResolver orderOwnershipResolver,
                                     CustomerOwnershipValidator customerOwnershipValidator) {
        this.orderLineService = orderLineService;
        this.orderOwnershipResolver = orderOwnershipResolver;
        this.customerOwnershipValidator = customerOwnershipValidator;
    }

    @PostMapping
    public ResponseEntity<CreateOrderLineResponse> proceed(@RequestBody CreateOrderLineRequest request){
        UUID resourceCustomerId = orderOwnershipResolver.resolveCustomerIdByOrderId(request.orderId());
        customerOwnershipValidator.validate(resourceCustomerId);
        CreateOrderLineCommand command = mapFromRequestToCommand(request);
        CreateOrderLineResult result = orderLineService.proceed(command);
        CreateOrderLineResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }
    private CreateOrderLineCommand mapFromRequestToCommand(CreateOrderLineRequest request){
      return CreateOrderLineCommand.builder()
              .orderId(request.orderId())
              .productId(request.productId())
              .quantity(request.quantity())
              .unitPrice(request.unitPrice())
              .categoryName(request.categoryName())
              .productName(request.productName())
              .providerName(request.providerName())
              .productProviderReference(request.productProviderReference())
              .build();
    }
    private CreateOrderLineResponse mapFromResultToResponse(CreateOrderLineResult result){
        return CreateOrderLineResponse.builder().id(result.id()).build();
    }
}
