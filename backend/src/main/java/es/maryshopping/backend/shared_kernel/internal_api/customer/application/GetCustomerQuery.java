package es.maryshopping.backend.shared_kernel.internal_api.customer.application;

import java.util.UUID;

public record GetCustomerQuery(
        UUID customerId) {
}
