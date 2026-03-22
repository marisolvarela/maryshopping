package es.maryshopping.backend.orders.usecases.create_order_line.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateOrderLineResponse(
        UUID id
) {
}
