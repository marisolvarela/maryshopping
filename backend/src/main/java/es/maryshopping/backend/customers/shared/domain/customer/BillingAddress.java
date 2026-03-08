package es.maryshopping.backend.customers.shared.domain.customer;

import es.maryshopping.backend.shared_kernel.domain.address.Address;
import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;


public record BillingAddress(Address value) {
    public BillingAddress {
        if (value == null) {
            throw new BusinessRuleException("BillingAddress cannot be null");
        }
    }

}
