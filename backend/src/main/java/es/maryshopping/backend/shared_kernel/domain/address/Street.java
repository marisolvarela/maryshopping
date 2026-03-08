package es.maryshopping.backend.shared_kernel.domain.address;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record Street(String value) {
    public Street{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("Street cannot be null or blank");
        }
    }
}
