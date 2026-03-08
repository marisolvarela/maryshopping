package es.maryshopping.backend.customers.usecases.get_all_customers.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerResult(
        UUID id,
        String firstName,
        String lastName,
        String dni,
        String phoneNumber,
        String emailAddress,
        String billingAddressStreet,
        String billingAddressCity,
        String billingAddressProvince,
        String billingAddressPostalCode,
        String billingAddressCountry,
        String shippingAddressStreet,
        String shippingAddressCity,
        String shippingAddressProvince,
        String shippingAddressPostalCode,
        String shippingAddressCountry
) {
}

