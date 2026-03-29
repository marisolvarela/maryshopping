package es.maryshopping.backend.customers.usecases.get_customer.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetCustomerResponse(
        UUID id,
        String firstName,
        String lastName,
        String dni,
        String phoneNumber,
        String emailAddress,
        AddressResponse billingAddress,
        AddressResponse shippingAddress
) {
    @Builder
public record AddressResponse(
            String street,
            String city,
            String province,
            String postalCode,
            String country
    ){
    }

}
