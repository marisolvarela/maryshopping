package es.maryshopping.backend.orders.shared.domain.order;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record OrderDate(Long value) {
    public OrderDate{
            if (value == null) {
                throw new BusinessRuleException("OrderDate cannot be null");
            }
            if(value < 0){
                throw new BusinessRuleException("OrderDate cannot be < than 0");
            }
    }
}
