package es.maryshopping.backend.orders.usecases.delete_order.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteOrderResponse(
        UUID id
) {
}
