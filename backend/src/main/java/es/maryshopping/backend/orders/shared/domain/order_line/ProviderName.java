package es.maryshopping.backend.orders.shared.domain.order_line;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record ProviderName(String value) {
    public ProviderName {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("ProviderName cannot be null or blank");
        }
    }
}
