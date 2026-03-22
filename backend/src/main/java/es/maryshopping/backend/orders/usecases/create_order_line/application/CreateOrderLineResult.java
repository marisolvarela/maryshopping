package es.maryshopping.backend.orders.usecases.create_order_line.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateOrderLineResult(
        UUID id
) {
}
