package es.maryshopping.backend.orders.shared.domain.order_line;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

import java.math.BigDecimal;

public record UnitPrice(BigDecimal value) {
    public UnitPrice {
        if (value == null) {
            throw new BusinessRuleException("Price cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Price must be greater than 0");
        }
    }
}
