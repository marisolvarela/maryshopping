package es.maryshopping.backend.customers.shared.domain.customer;

import lombok.Builder;

@Builder
public record Customer(
        CustomerId id,
        FirstName firstName,
        LastName lastName,
        NationalIdentificationNumber dni,
        BillingAddress billingAddress,
        ShippingAddress shippingAddress,
        PhoneNumber phoneNumber,
        EmailAddress emailAddress
) {
}
