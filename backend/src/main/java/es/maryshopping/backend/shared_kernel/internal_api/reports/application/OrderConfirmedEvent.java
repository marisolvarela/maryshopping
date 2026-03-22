package es.maryshopping.backend.shared_kernel.internal_api.reports.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID orderId,
        Long orderDate,
        UUID customerId,
        String customerFirstName,
        String customerLastName,
        String customerDni,
        String shippingStreet,
        String shippingCity,
        String shippingProvince,
        String shippingPostalCode,
        String shippingCountry,
        BigDecimal totalAmountDue,
        List<OrderLineEvent> orderLineEvents
) {
    public record OrderLineEvent(
            UUID orderLineId,
            UUID productId,
            String productName,
            String productProviderReference,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal subTotal,
            String categoryName,
            String providerName
    ) {
    }
}
