package es.maryshopping.backend.customers.usecases.create_customer.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCustomerResult(
        UUID id
) {
}
