package es.maryshopping.backend.orders.shared.domain.order;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record Stock(Integer value) {
    public Stock{
        if(value == null){
            throw new BusinessRuleException("Stock cannot be null");
        }
        if(value <= 0){
            throw new BusinessRuleException("Stock must be greater than 0");
        }
    }
}

