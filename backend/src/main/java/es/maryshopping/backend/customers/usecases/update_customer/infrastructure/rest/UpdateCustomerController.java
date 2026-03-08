package es.maryshopping.backend.customers.usecases.update_customer.infrastructure.rest;

import es.maryshopping.backend.customers.usecases.update_customer.application.UpdateCustomerCommand;
import es.maryshopping.backend.customers.usecases.update_customer.application.UpdateCustomerResult;
import es.maryshopping.backend.customers.usecases.update_customer.application.UpdateCustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class UpdateCustomerController {
    private final UpdateCustomerService updateCustomerService;

    public UpdateCustomerController(UpdateCustomerService updateCustomerService) {
        this.updateCustomerService = updateCustomerService;
    }
    @PutMapping("/{customerId}")
    public ResponseEntity<UpdateCustomerResponse> proceed(@PathVariable UUID customerId, @RequestBody UpdateCustomerRequest request) {
        UpdateCustomerCommand command = mapFromRequestToCommand(customerId, request);
        UpdateCustomerResult result = updateCustomerService.proceed(command);
        UpdateCustomerResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private UpdateCustomerCommand mapFromRequestToCommand(UUID customerId, UpdateCustomerRequest request) {
        UpdateCustomerCommand.AddressCommand billingAddress = UpdateCustomerCommand.AddressCommand.builder()
                .street(request.billingAddress().street())
                .city(request.billingAddress().city())
                .province(request.billingAddress().province())
                .postalCode(request.billingAddress().postalCode())
                .country(request.billingAddress().country())
                .build();

        UpdateCustomerCommand.AddressCommand shippingAddress = UpdateCustomerCommand.AddressCommand.builder()
                .street(request.shippingAddress().street())
                .city(request.shippingAddress().city())
                .province(request.shippingAddress().province())
                .postalCode(request.shippingAddress().postalCode())
                .country(request.shippingAddress().country())
                .build();

        return UpdateCustomerCommand.builder()
                .customerId(customerId)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .dni(request.dni())
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .phoneNumber(request.phoneNumber())
                .emailAddress(request.emailAddress())
                .build();
    }
    private UpdateCustomerResponse mapFromResultToResponse(UpdateCustomerResult result) {
        return UpdateCustomerResponse.builder().id(result.id()).build();
    }
}
