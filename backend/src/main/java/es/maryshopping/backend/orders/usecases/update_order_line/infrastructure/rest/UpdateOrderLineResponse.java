package es.maryshopping.backend.orders.usecases.update_order_line.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateOrderLineResponse(
        UUID orderLineId
) {
}
