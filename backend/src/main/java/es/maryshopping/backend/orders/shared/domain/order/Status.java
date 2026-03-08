package es.maryshopping.backend.orders.shared.domain.order;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record Status(StatusEnum value) {

    public Status {
        if (value == null) {
            throw new BusinessRuleException("Status value cannot be null");
        }
    }
}
