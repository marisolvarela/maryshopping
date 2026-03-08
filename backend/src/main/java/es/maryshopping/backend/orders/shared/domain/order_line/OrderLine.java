package es.maryshopping.backend.orders.shared.domain.order_line;

import es.maryshopping.backend.orders.shared.domain.order.OrderId;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderLine(
        OrderLineId id,
        OrderId orderId,
        CategoryName categoryName,
        ProductId productId,
        ProductName productName,
        ProviderName providerName,
        ProductProviderReference productProviderReference,
        Quantity quantity,
        UnitPrice unitPrice
) {

    public SubTotal subTotal() {
        BigDecimal total = unitPrice.value().multiply(BigDecimal.valueOf(quantity.value()));
        return new SubTotal(total);
    }
}
