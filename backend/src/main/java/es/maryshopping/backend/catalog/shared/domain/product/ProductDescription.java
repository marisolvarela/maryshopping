package es.maryshopping.backend.catalog.shared.domain.product;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record ProductDescription(String value) {
    public ProductDescription {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("Description cannot be null or blank");
        }
    }
}
