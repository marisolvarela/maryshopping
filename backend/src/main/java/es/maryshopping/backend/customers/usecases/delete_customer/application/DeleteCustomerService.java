package es.maryshopping.backend.customers.usecases.delete_customer.application;


import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerEntity;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerSpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeleteCustomerService {
    private final CustomerSpringRepository repository;

    public DeleteCustomerService(CustomerSpringRepository repository) {
        this.repository = repository;
    }
    public DeleteCustomerResult proceed(DeleteCustomerCommand command){
        Optional<CustomerEntity> optionalCustomerEntity = repository.findById(command.id());
        if (optionalCustomerEntity.isEmpty()) {
            throw new EntityNotFoundException("Customer not found");
        }
        CustomerEntity customerEntity = optionalCustomerEntity.get();
        repository.deleteById(customerEntity.getId());
        return DeleteCustomerResult.builder().id(command.id()).build();
    }
}
