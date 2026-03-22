package es.maryshopping.backend.orders.usecases.set_order_as_confirmed.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SetOrderAsConfirmedResponse(
        UUID id
) {
}
