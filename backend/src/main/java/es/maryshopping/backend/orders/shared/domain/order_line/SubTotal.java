package es.maryshopping.backend.orders.shared.domain.order_line;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

import java.math.BigDecimal;

public record SubTotal(BigDecimal value) {
    public SubTotal{
        if (value == null) {
            throw new BusinessRuleException("SubTotal cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("SubTotal must be greater than 0");
        }
    }
}
