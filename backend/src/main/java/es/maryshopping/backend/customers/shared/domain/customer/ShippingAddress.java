package es.maryshopping.backend.customers.shared.domain.customer;

import es.maryshopping.backend.shared_kernel.domain.address.Address;
import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record ShippingAddress(Address value) {
    public ShippingAddress{
        if (value == null) {
            throw new BusinessRuleException("ShippingAddress cannot be null");
        }
    }
}
