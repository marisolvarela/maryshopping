package es.maryshopping.backend.orders.shared.domain.order_line;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record Quantity(Integer value) {
    public Quantity{
        if (value == null) {
            throw new BusinessRuleException("Quantity cannot be null");
        }
        if (value <= 0) {
            throw new BusinessRuleException("Quantity must be greater than 0");
        }
    }
}
