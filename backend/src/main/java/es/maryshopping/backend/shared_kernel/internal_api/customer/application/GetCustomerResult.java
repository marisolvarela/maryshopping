package es.maryshopping.backend.shared_kernel.internal_api.customer.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetCustomerResult(
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

