package es.maryshopping.backend.orders.usecases.create_order.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateOrderCommand(
        UUID customerId,
        Long orderDate,
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
