package es.maryshopping.backend.shared_kernel.domain.address;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record Province(String value) {
    public Province{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("Province cannot be null or blank");
        }
    }
}
