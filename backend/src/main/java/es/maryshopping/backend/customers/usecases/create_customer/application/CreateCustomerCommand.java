package es.maryshopping.backend.customers.usecases.create_customer.application;

import lombok.Builder;

@Builder
public record CreateCustomerCommand(
        String firstName,
        String lastName,
        String dni,
        String phoneNumber,
        String emailAddress,
        String password,
        AddressCommand billingAddress,
        AddressCommand shippingAddress
) {
    @Builder
    public record AddressCommand(
            String street,
            String city,
            String province,
            String postalCode,
            String country
    ) {
    }
}



