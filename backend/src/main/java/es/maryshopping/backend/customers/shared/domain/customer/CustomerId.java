package es.maryshopping.backend.customers.shared.domain.customer;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

import java.util.UUID;

public record CustomerId(UUID value) {
    public CustomerId{
        if (value == null) {
            throw new BusinessRuleException("CustomerId cannot be null");
        }
    }
}
