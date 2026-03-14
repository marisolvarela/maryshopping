package es.maryshopping.backend.shared_kernel.internal_api.product.infrastructure.internal;

import es.maryshopping.backend.shared_kernel.internal_api.product.application.GetProductQuery;
import es.maryshopping.backend.shared_kernel.internal_api.product.application.GetProductResult;

public interface GetProduct {

    GetProductResult proceed(GetProductQuery query);
}
