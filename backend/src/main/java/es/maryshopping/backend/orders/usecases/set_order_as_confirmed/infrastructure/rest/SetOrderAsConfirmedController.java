package es.maryshopping.backend.orders.usecases.set_order_as_confirmed.infrastructure.rest;


import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderOwnershipResolver;
import es.maryshopping.backend.orders.usecases.set_order_as_confirmed.application.SetOrderAsConfirmedCommand;
import es.maryshopping.backend.orders.usecases.set_order_as_confirmed.application.SetOrderAsConfirmedResult;
import es.maryshopping.backend.orders.usecases.set_order_as_confirmed.application.SetOrderAsConfirmedService;
import es.maryshopping.backend.shared_kernel.security.CustomerOwnershipValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders/order/status/confirmed")
public class SetOrderAsConfirmedController {
    private final SetOrderAsConfirmedService SetOrderAsConfirmedService;
    private final OrderOwnershipResolver orderOwnershipResolver;
    private final CustomerOwnershipValidator customerOwnershipValidator;

    public SetOrderAsConfirmedController(SetOrderAsConfirmedService SetOrderAsConfirmedService,
                                         OrderOwnershipResolver orderOwnershipResolver,
                                         CustomerOwnershipValidator customerOwnershipValidator) {
        this.SetOrderAsConfirmedService = SetOrderAsConfirmedService;
        this.orderOwnershipResolver = orderOwnershipResolver;
        this.customerOwnershipValidator = customerOwnershipValidator;
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<SetOrderAsConfirmedResponse> proceed(@PathVariable UUID orderId){
        UUID resourceCustomerId = orderOwnershipResolver.resolveCustomerIdByOrderId(orderId);
        customerOwnershipValidator.validate(resourceCustomerId);
        SetOrderAsConfirmedCommand command = mapFromRequestToCommand(orderId);
        SetOrderAsConfirmedResult result = SetOrderAsConfirmedService.proceed(command);
        SetOrderAsConfirmedResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }
    private SetOrderAsConfirmedCommand mapFromRequestToCommand(UUID orderId){
        return SetOrderAsConfirmedCommand.builder()
                .id(orderId)
                .build();
    }
    private SetOrderAsConfirmedResponse mapFromResultToResponse(SetOrderAsConfirmedResult result){
        return SetOrderAsConfirmedResponse.builder().id(result.id()).build();
    }
}
