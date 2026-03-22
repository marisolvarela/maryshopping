package es.maryshopping.backend.orders.usecases.update_order.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;
@Builder
public record UpdateOrderRequest(
        Long orderDate,
        UUID customerId,
        AddressRequest shippingAddress
) {
    @Builder
    public record AddressRequest(
            String street,
            String city,
            String province,
            String postalCode,
            String country
    ){
    }
}
