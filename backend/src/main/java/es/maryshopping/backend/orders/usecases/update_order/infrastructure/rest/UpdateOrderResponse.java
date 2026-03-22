package es.maryshopping.backend.orders.usecases.update_order.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateOrderResponse(
        UUID id
) {
}
