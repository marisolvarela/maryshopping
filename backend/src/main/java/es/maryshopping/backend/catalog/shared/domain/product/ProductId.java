package es.maryshopping.backend.catalog.shared.domain.product;


import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

import java.util.UUID;

public record ProductId(UUID value) {
    public ProductId {
        if (value == null ) {
            throw new BusinessRuleException("ProductId cannot be null");
        }
    }
}
