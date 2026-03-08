package es.maryshopping.backend.customers.shared.domain.authentication;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record Password(String value) {
    public Password{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("Password cannot be null or blank");
        }
    }
}
