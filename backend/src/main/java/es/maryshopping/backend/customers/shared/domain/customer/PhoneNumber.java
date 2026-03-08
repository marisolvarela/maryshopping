package es.maryshopping.backend.customers.shared.domain.customer;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record PhoneNumber(String value) {
    public PhoneNumber{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("PhoneNumber cannot be null or blank");
        }
    }
}
