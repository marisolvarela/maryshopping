package es.maryshopping.backend.orders.shared.domain.order_line;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record ProductName(String value) {
    public ProductName {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("ProductName cannot be null or blank");
        }
    }
}
