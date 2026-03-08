package es.maryshopping.backend.customers.usecases.delete_customer.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteCustomerResult(
        UUID id
) {
}
