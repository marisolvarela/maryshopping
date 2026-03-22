package es.maryshopping.backend.orders.usecases.create_order_line.infrastructure.rest;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderLineRequest(
        UUID orderId,
        UUID productId,
        Integer quantity,
        BigDecimal unitPrice,
        String categoryName,
        String productName,
        String providerName,
        String productProviderReference
) {
}
