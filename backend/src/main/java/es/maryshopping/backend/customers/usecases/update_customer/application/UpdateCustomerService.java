package es.maryshopping.backend.customers.usecases.update_customer.application;

import es.maryshopping.backend.customers.shared.domain.customer.*;

import es.maryshopping.backend.customers.shared.infrastructure.iam.IAM;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerEntity;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerSpringRepository;
import es.maryshopping.backend.shared_kernel.domain.address.*;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateCustomerService {

    private final CustomerSpringRepository repository;
    private final IAM iam;

    public UpdateCustomerService(CustomerSpringRepository repository, IAM iam) {
        this.repository = repository;
        this.iam = iam;
    }

    public UpdateCustomerResult proceed(UpdateCustomerCommand command) {
        // Step 1: validate the customer exists
        rejectIfCustomerNotFound(command.customerId());

        // Step 2: update user in IAM
        updateInIam(command);

        // Step 3: persist updated customer in database
        return saveCustomer(command);
    }

    // ─── Step 1 ───────────────────────────────────────────────────────────────

    private void rejectIfCustomerNotFound(UUID customerId) {
        if (repository.findById(customerId).isEmpty()) {
            throw new EntityNotFoundException("Customer not found");
        }
    }

    // ─── Step 2 ───────────────────────────────────────────────────────────────

    private void updateInIam(UpdateCustomerCommand command) {
        iam.updateUser(
                command.customerId().toString(),
                command.emailAddress(),
                command.emailAddress(),
                command.firstName(),
                command.lastName(),
                command.password()
        );
    }

    // ─── Step 3 ───────────────────────────────────────────────────────────────

    private UpdateCustomerResult saveCustomer(UpdateCustomerCommand command) {
        Customer customer = mapFromCommandToDomain(command);
        CustomerEntity saved = repository.save(CustomerEntity.fromDomain(customer));
        return UpdateCustomerResult.builder().id(saved.getId()).build();
    }

    private Customer mapFromCommandToDomain(UpdateCustomerCommand command) {
        return Customer.builder()
                .id(new CustomerId(command.customerId()))
                .firstName(new FirstName(command.firstName()))
                .lastName(new LastName(command.lastName()))
                .dni(new NationalIdentificationNumber(command.dni()))
                .billingAddress(new BillingAddress(buildAddressFrom(command.billingAddress())))
                .shippingAddress(new ShippingAddress(buildAddressFrom(command.shippingAddress())))
                .phoneNumber(new PhoneNumber(command.phoneNumber()))
                .emailAddress(new EmailAddress(command.emailAddress()))
                .build();
    }

    private Address buildAddressFrom(UpdateCustomerCommand.AddressCommand addressCommand) {
        return Address.builder()
                .street(new Street(addressCommand.street()))
                .city(new City(addressCommand.city()))
                .province(new Province(addressCommand.province()))
                .postalCode(new PostalCode(addressCommand.postalCode()))
                .country(new Country(addressCommand.country()))
                .build();
    }
}
