package es.maryshopping.backend.shared_kernel.domain.address;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record PostalCode(String value) {
    public PostalCode{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("PostalCode cannot be null or blank");
        }
    }
}
