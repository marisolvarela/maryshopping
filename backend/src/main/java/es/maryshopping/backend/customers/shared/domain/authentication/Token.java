package es.maryshopping.backend.customers.shared.domain.authentication;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record Token(String value) {
    public Token{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("Token cannot be null or blank");
        }
    }

}
