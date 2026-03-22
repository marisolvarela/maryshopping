package es.maryshopping.backend.orders.usecases.get_all_orders.infrastructure.rest;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record GetAllOrdersResponse(
        List<OrderResponse> orders
) {
    @Builder
    public record OrderResponse(
            UUID orderId,
            Long orderDate,
            UUID customerId,
            String firstName,
            String lastName,
            String dni,
            AddressResponse shippingAddress,
            List<OrderLineResponse> orderLines,
            BigDecimal totalAmountDue,
            String status
    ) {
        @Builder
        public record AddressResponse(
                String street,
                String city,
                String province,
                String postalCode,
                String country
        ) {
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
}

