package es.maryshopping.backend.orders.usecases.create_order.infrastructure.rest;

import java.util.UUID;

public record CreateOrderRequest(
        UUID customerId,
        Long orderDate,
        AddressRequest shippingAddress
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
