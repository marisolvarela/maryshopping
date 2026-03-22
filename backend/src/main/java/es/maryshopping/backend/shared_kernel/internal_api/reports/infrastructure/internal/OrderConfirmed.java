package es.maryshopping.backend.shared_kernel.internal_api.reports.infrastructure.internal;

import es.maryshopping.backend.shared_kernel.internal_api.reports.application.OrderConfirmedEvent;
import es.maryshopping.backend.shared_kernel.internal_api.reports.application.OrderConfirmedResult;

public interface OrderConfirmed {
    OrderConfirmedResult proceed(OrderConfirmedEvent event);
}
