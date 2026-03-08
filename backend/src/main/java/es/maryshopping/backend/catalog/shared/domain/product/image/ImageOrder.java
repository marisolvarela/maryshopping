package es.maryshopping.backend.catalog.shared.domain.product.image;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record ImageOrder(Integer value) {
    public ImageOrder {
        if (value == null) {
            throw new BusinessRuleException("ImageOrder cannot be null");
        }
        if(value < 0) {
            throw new BusinessRuleException("ImageOrder cannot be negative");
        }

    }
}
