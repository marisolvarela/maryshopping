package es.maryshopping.backend.orders.usecases.get_order.infrastructure.rest;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record GetOrderResponse(
        UUID orderId,
        Long orderDate,
        CustomerResponse customer,
        AddressResponse shippingAddress,
        List<OrderLineResponse> orderLines,
        BigDecimal totalAmountDue,
        String status
) {
    @Builder
    public record CustomerResponse(
        UUID customerId,
        String firstName,
        String lastName,
        String dni
    ) {
    }
    @Builder
    public record AddressResponse(
            String street,
            String city,
            String province,
            String postalCode,
            String country
    ){
    }
    @Builder
    public record OrderLineResponse(
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
