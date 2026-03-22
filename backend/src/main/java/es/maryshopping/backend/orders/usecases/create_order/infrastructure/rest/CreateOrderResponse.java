package es.maryshopping.backend.orders.usecases.create_order.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateOrderResponse(
        UUID id
) {
}
