package es.maryshopping.backend.customers.shared.domain.customer;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record EmailAddress(String value) {
    public EmailAddress{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("EmailAddress cannot be null or blank");
        }
    }
}
