package es.maryshopping.backend.customers.usecases.create_customer.infrastructure.rest;

public record CreateCustomerRequest(
        String firstName,
        String lastName,
        String dni,
        AddressRequest billingAddress,
        AddressRequest shippingAddress,
        String phoneNumber,
        String emailAddress,
        String password
) {
    public record AddressRequest(
            String street,
            String city,
            String province,
            String postalCode,
            String country
    ) {
    }
}
