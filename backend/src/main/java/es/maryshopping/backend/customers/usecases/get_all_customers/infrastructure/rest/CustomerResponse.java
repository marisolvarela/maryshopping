package es.maryshopping.backend.customers.usecases.get_all_customers.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerResponse(
        UUID id,
        String firstName,
        String lastName,
        String dni,
        String phoneNumber,
        String emailAddress,
        AddressResponse billingAddress,
        AddressResponse ShippingAddress
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
