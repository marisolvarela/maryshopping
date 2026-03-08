package es.maryshopping.backend.shared_kernel.domain.address;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record Country(String value) {
    public Country{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("Country cannot be null or blank");
        }
    }
}
