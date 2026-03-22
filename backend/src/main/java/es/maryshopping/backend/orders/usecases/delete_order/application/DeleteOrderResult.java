package es.maryshopping.backend.orders.usecases.delete_order.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteOrderResult(
        UUID id
) {
}
