package es.maryshopping.backend.customers.usecases.update_customer.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateCustomerResult(
        UUID id
) {
}
