package es.maryshopping.backend.customers.usecases.delete_customer.application;


import es.maryshopping.backend.customers.shared.infrastructure.iam.IAM;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerEntity;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerSpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteCustomerService {

    private final CustomerSpringRepository repository;
    private final IAM iam;

    public DeleteCustomerService(CustomerSpringRepository repository, IAM iam) {
        this.repository = repository;
        this.iam = iam;
    }

    public DeleteCustomerResult proceed(DeleteCustomerCommand command) {
        // Step 1: validate the customer exists
        CustomerEntity customer = findCustomerOrThrow(command.id());

        // Step 2: delete from IAM
        deleteFromIam(customer.getId());

        // Step 3: delete from database
        deleteFromDatabase(customer.getId());

        return DeleteCustomerResult.builder().id(command.id()).build();
    }

    // ─── Step 1 ───────────────────────────────────────────────────────────────

    private CustomerEntity findCustomerOrThrow(UUID customerId) {
        return repository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }

    // ─── Step 2 ───────────────────────────────────────────────────────────────

    private void deleteFromIam(UUID customerId) {
        iam.deleteUser(customerId.toString());
    }

    // ─── Step 3 ───────────────────────────────────────────────────────────────

    private void deleteFromDatabase(UUID customerId) {
        repository.deleteById(customerId);
    }
}
