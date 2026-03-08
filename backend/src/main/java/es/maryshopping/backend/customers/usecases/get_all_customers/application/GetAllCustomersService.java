package es.maryshopping.backend.customers.usecases.get_all_customers.application;


import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerEntity;
import es.maryshopping.backend.customers.shared.infrastructure.persistence.CustomerSpringRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllCustomersService {
    private final CustomerSpringRepository repository;

    public GetAllCustomersService(CustomerSpringRepository repository) {
        this.repository = repository;
    }

    public List<CustomerResult> proceed(GetAllCustomersQuery query) {
        List<CustomerEntity> customerEntities = repository.findAll();
        return customerEntities.stream()
                .map(this::fromEntityToResult)
                .toList();
    }

    private CustomerResult fromEntityToResult(CustomerEntity customerEntity) {
        return CustomerResult.builder()
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
