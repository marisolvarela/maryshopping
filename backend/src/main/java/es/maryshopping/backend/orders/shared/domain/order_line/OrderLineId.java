package es.maryshopping.backend.orders.shared.domain.order_line;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

import java.util.UUID;

public record OrderLineId(UUID value) {
    public OrderLineId{
        if (value == null) {
            throw new BusinessRuleException("OrderLineId cannot be null");
        }
    }
}
