package es.maryshopping.backend.orders.usecases.set_order_as_confirmed.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SetOrderAsConfirmedCommand(
        UUID id
) {
}
