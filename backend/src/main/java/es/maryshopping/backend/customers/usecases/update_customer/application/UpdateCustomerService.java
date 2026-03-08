package es.maryshopping.backend.customers.usecases.update_customer.application;

import es.maryshopping.backend.customers.shared.domain.customer.*;

import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerEntity;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerSpringRepository;
import es.maryshopping.backend.shared_kernel.domain.address.*;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateCustomerService {
    private final CustomerSpringRepository repository;

    public UpdateCustomerService(CustomerSpringRepository repository) {
        this.repository = repository;
    }

    public UpdateCustomerResult proceed(UpdateCustomerCommand command) {
        Optional<CustomerEntity> optionalCustomerEntity = repository.findById(command.customerId());
        if (optionalCustomerEntity.isEmpty()) {
            throw new EntityNotFoundException("Customer not found");
        }
        Customer customer = mapFromCommandToDomain(command);
        CustomerEntity entity = CustomerEntity.fromDomain(customer);
        CustomerEntity saved = repository.save(entity);
        return UpdateCustomerResult.builder().id(saved.getId()).build();
    }

    private Customer mapFromCommandToDomain(UpdateCustomerCommand command) {
        Address billingAddress = Address.builder()
                .street(new Street(command.billingAddress().street()))
                .city(new City(command.billingAddress().city()))
                .province(new Province(command.billingAddress().province()))
                .postalCode(new PostalCode(command.billingAddress().postalCode()))
                .country(new Country(command.billingAddress().country()))
                .build();
        Address shippingAddress = Address.builder()
                .street(new Street(command.shippingAddress().street()))
                .city(new City(command.shippingAddress().city()))
                .province(new Province(command.shippingAddress().province()))
                .postalCode(new PostalCode(command.shippingAddress().postalCode()))
                .country(new Country(command.shippingAddress().country()))
                .build();

        return Customer.builder()
                .id(new CustomerId(command.customerId()))
                .firstName(new FirstName(command.firstName()))
                .lastName(new LastName(command.lastName()))
                .dni(new NationalIdentificationNumber(command.dni()))
                .billingAddress(new BillingAddress(billingAddress))
                .shippingAddress(new ShippingAddress(shippingAddress))
                .phoneNumber(new PhoneNumber(command.phoneNumber()))
                .emailAddress(new EmailAddress(command.emailAddress()))
                .build();
    }
}
