package es.maryshopping.backend.orders.usecases.create_order.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateOrderResult(
        UUID id
) {
}
