package es.maryshopping.backend.customers.shared.domain.customer;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record LastName(String value) {
    public LastName{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("LastName cannot be null or blank");
        }
    }
}
