package es.maryshopping.backend.customers.usecases.delete_customer.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteCustomerResponse(
        UUID id
) {
}
