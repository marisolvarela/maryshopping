package es.maryshopping.backend.catalog.shared.domain.provider;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

import java.util.UUID;

public record ProviderId(UUID value) {
    public ProviderId {
        if (value == null) {
            throw new BusinessRuleException("ProductId cannot be null");
        }
    }
}
