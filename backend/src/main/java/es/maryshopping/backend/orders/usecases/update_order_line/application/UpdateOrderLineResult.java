package es.maryshopping.backend.orders.usecases.update_order_line.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateOrderLineResult(
        UUID orderLineId
) {
}
