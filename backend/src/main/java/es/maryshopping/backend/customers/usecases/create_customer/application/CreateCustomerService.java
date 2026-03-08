package es.maryshopping.backend.customers.usecases.create_customer.application;

import es.maryshopping.backend.customers.shared.domain.customer.*;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerEntity;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerSpringRepository;
import es.maryshopping.backend.shared_kernel.domain.address.*;
import es.maryshopping.backend.shared_kernel.exceptions.DuplicatedEntityException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CreateCustomerService {

    private final CustomerSpringRepository repository;

    public CreateCustomerService(CustomerSpringRepository repository) {
        this.repository = repository;
    }

    public CreateCustomerResult proceed(CreateCustomerCommand command) {
        Optional<CustomerEntity> optionalByDni = repository.findByDni(command.dni());
        if (optionalByDni.isPresent()) {
            throw  new DuplicatedEntityException("A customer with that dni already exits");
        }

        Optional<CustomerEntity> optionalByEmailAddress = repository.findByEmailAddress(command.emailAddress());
        if (optionalByEmailAddress.isPresent()){
            throw  new DuplicatedEntityException("A customer with that dni already exits");
        }
        UUID customerId = UUID.randomUUID();
        Customer customer = fromCommandToDomain(customerId, command);
        CustomerEntity entity = CustomerEntity.fromDomain(customer);
        CustomerEntity saved = repository.save(entity);
        return CreateCustomerResult.builder().id(saved.getId()).build();
    }

    private Customer fromCommandToDomain(UUID customerId, CreateCustomerCommand command) {
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
                .id(new CustomerId(customerId))
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
