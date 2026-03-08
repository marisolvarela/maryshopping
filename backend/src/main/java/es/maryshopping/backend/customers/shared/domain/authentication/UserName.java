package es.maryshopping.backend.customers.shared.domain.authentication;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record UserName(String value) {
    public UserName{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("UserName cannot be null or blank");
        }
    }
}
