package es.maryshopping.backend.orders.shared.domain.order_line;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

import java.util.UUID;

public record ProductId(UUID value) {
    public ProductId{
        if (value == null || value.toString().isBlank()) {
            throw new BusinessRuleException("ProductId cannot be null or blank");
        }
    }
}
