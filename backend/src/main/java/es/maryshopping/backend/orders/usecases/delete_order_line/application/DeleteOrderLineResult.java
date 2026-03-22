package es.maryshopping.backend.orders.usecases.delete_order_line.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteOrderLineResult(
        UUID id
) {
}
