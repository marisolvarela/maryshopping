package es.maryshopping.backend.orders.usecases.get_order.application;

import java.util.UUID;

public record GetOrderQuery(
        UUID orderId
) {
}
