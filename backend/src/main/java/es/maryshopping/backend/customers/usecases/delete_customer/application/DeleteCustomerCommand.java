package es.maryshopping.backend.customers.usecases.delete_customer.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteCustomerCommand(
        UUID id
) {
}
