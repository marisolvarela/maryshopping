package es.maryshopping.backend.orders.usecases.update_order_line.infrastructure.rest;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateOrderLineRequest(
        UUID orderId,
        String categoryName,
        UUID productId,
        String productName,
        String providerName,
        String  productProviderReference,
        Integer quantity,
        BigDecimal unitPrice
) {
}
