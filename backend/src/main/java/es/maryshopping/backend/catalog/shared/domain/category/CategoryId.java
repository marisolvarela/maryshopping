package es.maryshopping.backend.catalog.shared.domain.category;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

import java.util.UUID;

public record CategoryId(UUID value) {
    public CategoryId {
        if (value == null) {
            throw new BusinessRuleException("CategoryId cannot be null");
        }
    }
}
