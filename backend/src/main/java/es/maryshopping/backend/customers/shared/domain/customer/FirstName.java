package es.maryshopping.backend.customers.shared.domain.customer;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record FirstName(String value) {
    public FirstName{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("FirstName cannot be null or blank");
        }
    }
}
