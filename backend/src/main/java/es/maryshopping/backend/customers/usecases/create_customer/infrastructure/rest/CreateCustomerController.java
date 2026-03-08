package es.maryshopping.backend.customers.usecases.create_customer.infrastructure.rest;

import es.maryshopping.backend.customers.usecases.create_customer.application.CreateCustomerCommand;
import es.maryshopping.backend.customers.usecases.create_customer.application.CreateCustomerResult;
import es.maryshopping.backend.customers.usecases.create_customer.application.CreateCustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CreateCustomerController {
    private final CreateCustomerService createCustomerService;

    public CreateCustomerController(CreateCustomerService createCustomerService) {
        this.createCustomerService = createCustomerService;
    }

    @PostMapping
    public ResponseEntity<CreateCustomerResponse> proceed(@RequestBody CreateCustomerRequest request) {
        CreateCustomerCommand command = mapFromRequestToCommand(request);
        CreateCustomerResult result = createCustomerService.proceed(command);
        CreateCustomerResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);

    }
    private CreateCustomerCommand mapFromRequestToCommand(CreateCustomerRequest request) {

        CreateCustomerCommand.AddressCommand billingAddress = CreateCustomerCommand.AddressCommand.builder()
                .street(request.billingAddress().street())
                .city(request.billingAddress().city())
                .province(request.billingAddress().province())
                .postalCode(request.billingAddress().postalCode())
                .country(request.billingAddress().country())
                .build();

        CreateCustomerCommand.AddressCommand shippingAddress = CreateCustomerCommand.AddressCommand.builder()
                .street(request.shippingAddress().street())
                .city(request.shippingAddress().city())
                .province(request.shippingAddress().province())
                .postalCode(request.shippingAddress().postalCode())
                .country(request.shippingAddress().country())
                .build();

        return CreateCustomerCommand.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .dni(request.dni())
                .phoneNumber(request.phoneNumber())
                .emailAddress(request.emailAddress())
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .build();
    }
    private CreateCustomerResponse mapFromResultToResponse(CreateCustomerResult result) {
        return CreateCustomerResponse.builder().id(result.id()).build();
    }
}
