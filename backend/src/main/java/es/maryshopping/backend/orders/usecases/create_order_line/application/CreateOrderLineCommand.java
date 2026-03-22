package es.maryshopping.backend.orders.usecases.create_order_line.application;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;
@Builder
public record CreateOrderLineCommand(
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
