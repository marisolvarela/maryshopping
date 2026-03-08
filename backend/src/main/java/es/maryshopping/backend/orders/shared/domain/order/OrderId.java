package es.maryshopping.backend.orders.shared.domain.order;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

import java.util.UUID;

public record OrderId(UUID value) {
    public OrderId{
        if (value == null) {
            throw new BusinessRuleException("OrderId cannot be null");
        }
    }
}
