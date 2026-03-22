package es.maryshopping.backend.customers.usecases.create_customer.application;

import es.maryshopping.backend.customers.shared.domain.customer.*;
import es.maryshopping.backend.customers.shared.infrastructure.iam.IAM;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerEntity;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerSpringRepository;
import es.maryshopping.backend.shared_kernel.domain.address.*;
import es.maryshopping.backend.shared_kernel.exceptions.DuplicatedEntityException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateCustomerService {

    private static final String CUSTOMER_ROLE = "customer";

    private final CustomerSpringRepository repository;
    private final IAM iam;

    public CreateCustomerService(CustomerSpringRepository repository, IAM iam) {
        this.repository = repository;
        this.iam = iam;
    }

    public CreateCustomerResult proceed(CreateCustomerCommand command) {
        // Step 1: enforce uniqueness constraints
        rejectIfDniAlreadyExists(command.dni());
        rejectIfEmailAlreadyExists(command.emailAddress());

        // Step 2: register user in IAM
        UUID customerId = registerInIam(command);

        // Step 3: persist customer in database
        return saveCustomer(customerId, command);
    }

    // ─── Step 1 ───────────────────────────────────────────────────────────────

    private void rejectIfDniAlreadyExists(String dni) {
        if (repository.findByDni(dni).isPresent()) {
            throw new DuplicatedEntityException("A customer with that dni already exits");
        }
    }

    private void rejectIfEmailAlreadyExists(String emailAddress) {
        if (repository.findByEmailAddress(emailAddress).isPresent()) {
            throw new DuplicatedEntityException("A customer with that email address already exits");
        }
    }

    // ─── Step 2 ───────────────────────────────────────────────────────────────

    private UUID registerInIam(CreateCustomerCommand command) {
        String iamUserId = iam.createUser(
                command.emailAddress(),
                command.emailAddress(),
                command.firstName(),
                command.lastName(),
                command.password(),
                CUSTOMER_ROLE
        );
        return parseIamUserIdAsUUID(iamUserId);
    }

    // ─── Step 3 ───────────────────────────────────────────────────────────────

    private CreateCustomerResult saveCustomer(UUID customerId, CreateCustomerCommand command) {
        Customer customer = fromCommandToDomain(customerId, command);
        CustomerEntity saved = repository.save(CustomerEntity.fromDomain(customer));
        return CreateCustomerResult.builder().id(saved.getId()).build();
    }

    private UUID parseIamUserIdAsUUID(String iamUserId) {
        try {
            return UUID.fromString(iamUserId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid IAM user ID format (expected UUID): " + iamUserId, e);
        }
    }

    private Customer fromCommandToDomain(UUID customerId, CreateCustomerCommand command) {
        return Customer.builder()
                .id(new CustomerId(customerId))
                .firstName(new FirstName(command.firstName()))
                .lastName(new LastName(command.lastName()))
                .dni(new NationalIdentificationNumber(command.dni()))
                .billingAddress(new BillingAddress(buildAddressFrom(command.billingAddress())))
                .shippingAddress(new ShippingAddress(buildAddressFrom(command.shippingAddress())))
                .phoneNumber(new PhoneNumber(command.phoneNumber()))
                .emailAddress(new EmailAddress(command.emailAddress()))
                .build();
    }

    private Address buildAddressFrom(CreateCustomerCommand.AddressCommand addressCommand) {
        return Address.builder()
                .street(new Street(addressCommand.street()))
                .city(new City(addressCommand.city()))
                .province(new Province(addressCommand.province()))
                .postalCode(new PostalCode(addressCommand.postalCode()))
                .country(new Country(addressCommand.country()))
                .build();
    }
}
