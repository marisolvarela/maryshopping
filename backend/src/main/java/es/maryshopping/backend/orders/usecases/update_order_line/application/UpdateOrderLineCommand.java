package es.maryshopping.backend.orders.usecases.update_order_line.application;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;
@Builder
public record UpdateOrderLineCommand(
        UUID id,
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
