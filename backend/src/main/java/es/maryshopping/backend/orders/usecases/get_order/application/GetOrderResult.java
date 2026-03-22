package es.maryshopping.backend.orders.usecases.get_order.application;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record GetOrderResult(
        UUID orderId,
        Long orderDate,
        CustomerResult customer,
        AddressResult shippingAddress,
        List<OrderLineResult> orderLines,
        BigDecimal totalAmountDue,
        String status
) {
    @Builder
    public record CustomerResult(
        UUID customerId,
        String firstName,
        String lastName,
        String dni
    ) {
    }
    @Builder
    public record AddressResult(
            String street,
            String city,
            String province,
            String postalCode,
            String country
    ){
    }
    @Builder
    public record OrderLineResult(
            UUID orderLineId,
            UUID orderId,
            UUID productId,
            String productName,
            String productProviderReference,
            String categoryName,
            String providerName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal subTotal
    ) {
    }
}
