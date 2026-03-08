package es.maryshopping.backend.orders.shared.domain.order_line;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record CategoryName(String value) {
    public CategoryName {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("CategoryName cannot be null or blank");
        }
    }
}
