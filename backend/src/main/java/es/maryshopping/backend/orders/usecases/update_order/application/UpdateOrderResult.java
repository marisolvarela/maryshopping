package es.maryshopping.backend.orders.usecases.update_order.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateOrderResult(
        UUID id
) {
}
