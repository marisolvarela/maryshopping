package es.maryshopping.backend.customers.usecases.update_customer.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateCustomerCommand(
        UUID customerId,
        String firstName,
        String lastName,
        String dni,
        AddressCommand billingAddress,
        AddressCommand shippingAddress,
        String phoneNumber,
        String emailAddress
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
