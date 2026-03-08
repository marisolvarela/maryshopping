package es.maryshopping.backend.catalog.shared.domain.product;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record ProductName(String value) {
    public ProductName {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("ProductName cannot be null or blank");
        }
    }
}
