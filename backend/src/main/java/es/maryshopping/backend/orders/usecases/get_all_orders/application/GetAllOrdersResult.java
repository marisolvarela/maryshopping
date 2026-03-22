package es.maryshopping.backend.orders.usecases.get_all_orders.application;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record GetAllOrdersResult(
        List<OrderResult> orders
) {
    @Builder
    public record OrderResult(
            UUID orderId,
            Long orderDate,
            UUID customerId,
            String firstName,
            String lastName,
            String dni,
            AddressResult shippingAddress,
            List<OrderLineResult> orderLines,
            BigDecimal totalAmountDue,
            String status
    ) {
        @Builder
        public record AddressResult(
                String street,
                String city,
                String province,
                String postalCode,
                String country
        ) {
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
}

