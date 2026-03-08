package es.maryshopping.backend.catalog.shared.domain.product;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

import java.math.BigDecimal;

public record Price(BigDecimal value) {
    public Price {
        if (value == null) {
            throw new BusinessRuleException("Price cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Price must be greater than 0");
        }
    }
}
