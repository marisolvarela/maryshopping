package es.maryshopping.backend.customers.usecases.update_customer.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateCustomerResponse(
        UUID id
) {
}
