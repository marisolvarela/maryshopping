package es.maryshopping.backend.orders.usecases.update_order.application;

import lombok.Builder;

import java.util.UUID;
@Builder
public record UpdateOrderCommand(
        UUID id,
        Long orderDate,
        UUID customerId,
       AddressCommand shippingAddress
) {
    @Builder
    public record AddressCommand(
            String street,
            String city,
            String province,
            String postalCode,
            String country
    ){
    }
}
