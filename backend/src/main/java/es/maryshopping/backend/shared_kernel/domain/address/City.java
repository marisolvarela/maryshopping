package es.maryshopping.backend.shared_kernel.domain.address;

import es.maryshopping.backend.shared_kernel.exceptions.BusinessRuleException;

public record City(String value) {
    public City{
        if(value == null || value.isBlank()){
            throw new BusinessRuleException("City cannot be null or blank");
        }
    }

}
