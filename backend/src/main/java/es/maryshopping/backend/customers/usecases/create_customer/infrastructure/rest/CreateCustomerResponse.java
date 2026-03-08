package es.maryshopping.backend.customers.usecases.create_customer.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCustomerResponse(
           UUID id
) {
}
