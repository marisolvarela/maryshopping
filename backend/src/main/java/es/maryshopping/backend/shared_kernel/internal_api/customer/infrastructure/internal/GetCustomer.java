package es.maryshopping.backend.shared_kernel.internal_api.customer.infrastructure.internal;

import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerQuery;
import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerResult;

public interface GetCustomer {

    public GetCustomerResult proceed(GetCustomerQuery query);
}
