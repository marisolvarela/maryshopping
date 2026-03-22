package es.maryshopping.backend.orders.shared.domain.order;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

import java.math.BigDecimal;

public record TotalAmountDue(BigDecimal value) {
    public TotalAmountDue{
        if (value == null) {
            throw new BusinessRuleException("TotalAmountDue cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("TotalAmountDue cannot be negative");
        }
    }
}
