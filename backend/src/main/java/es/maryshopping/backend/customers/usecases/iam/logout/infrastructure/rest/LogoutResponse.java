package es.maryshopping.backend.customers.usecases.iam.logout.infrastructure.rest;

public record LogoutResponse(
        boolean success,
        String message
) {
}

