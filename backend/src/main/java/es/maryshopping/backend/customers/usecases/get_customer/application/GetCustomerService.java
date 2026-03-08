package es.maryshopping.backend.customers.usecases.get_customer.application;


import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerEntity;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerSpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerQuery;
import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerResult;
import es.maryshopping.backend.shared_kernel.internal_api.customer.infrastructure.internal.GetCustomer;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetCustomerService implements GetCustomer {
    private final CustomerSpringRepository repository;

    public GetCustomerService(CustomerSpringRepository repository) {
        this.repository = repository;
    }

    public GetCustomerResult proceed(GetCustomerQuery query){
        Optional<CustomerEntity> optionalCustomerEntity = repository.findById(query.customerId());
        if (optionalCustomerEntity.isEmpty()) {
            throw new EntityNotFoundException("Customer not found");
        }
        CustomerEntity customerEntity = optionalCustomerEntity.get();
        return GetCustomerResult.builder()
                .id(customerEntity.getId())
                .firstName(customerEntity.getFirstName())
                .lastName(customerEntity.getLastName())
                .dni(customerEntity.getDni())
                .phoneNumber(customerEntity.getPhoneNumber())
                .emailAddress(customerEntity.getEmailAddress())
                .billingAddressStreet(customerEntity.getBillingAddressStreet())
                .billingAddressCity(customerEntity.getBillingAddressCity())
                .billingAddressProvince(customerEntity.getBillingAddressProvince())
                .billingAddressPostalCode(customerEntity.getBillingAddressPostalCode())
                .billingAddressCountry(customerEntity.getBillingAddressCountry())
                .shippingAddressStreet(customerEntity.getShippingAddressStreet())
                .shippingAddressCity(customerEntity.getShippingAddressCity())
                .shippingAddressProvince(customerEntity.getShippingAddressProvince())
                .shippingAddressPostalCode(customerEntity.getShippingAddressPostalCode())
                .shippingAddressCountry(customerEntity.getShippingAddressCountry())
                .build();
    }
}
