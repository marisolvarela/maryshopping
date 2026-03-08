package es.maryshopping.backend.customers.usecases.delete_customer.infrastructure.rest;

import es.maryshopping.backend.customers.usecases.delete_customer.application.DeleteCustomerCommand;
import es.maryshopping.backend.customers.usecases.delete_customer.application.DeleteCustomerResult;
import es.maryshopping.backend.customers.usecases.delete_customer.application.DeleteCustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class DeleteCustomerController {
    private final DeleteCustomerService deleteCustomerService;

    public DeleteCustomerController(DeleteCustomerService deleteCustomerService) {
        this.deleteCustomerService = deleteCustomerService;
    }
    @DeleteMapping("/{customerId}")
    public ResponseEntity<DeleteCustomerResponse> proceed(@PathVariable UUID customerId){
        DeleteCustomerCommand command = mapFromPathToCommand(customerId);
        DeleteCustomerResult result = deleteCustomerService.proceed(command);
        DeleteCustomerResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);

    }
    private DeleteCustomerCommand mapFromPathToCommand(UUID customerId){
        return DeleteCustomerCommand.builder()
                .id(customerId)
                .build();
    }
    private DeleteCustomerResponse mapFromResultToResponse(DeleteCustomerResult result){
        return DeleteCustomerResponse.builder().id(result.id()).build();
    }
}
