package es.maryshopping.backend.catalog.shared.domain.product;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record ProductProviderReference(String value) {
    public ProductProviderReference {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("ProviderReference cannot be null or blank");
        }
    }
}
