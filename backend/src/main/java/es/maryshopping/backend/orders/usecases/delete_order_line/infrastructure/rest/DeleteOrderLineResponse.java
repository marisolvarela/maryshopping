package es.maryshopping.backend.orders.usecases.delete_order_line.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteOrderLineResponse(
        UUID id
) {
}
