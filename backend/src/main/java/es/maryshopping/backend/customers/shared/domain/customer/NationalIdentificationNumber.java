package es.maryshopping.backend.customers.shared.domain.customer;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record NationalIdentificationNumber(String value) {
    public NationalIdentificationNumber{
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("NationalIdentificationNumber cannot be null or blank");
        }
    }
}
