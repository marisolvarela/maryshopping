package es.maryshopping.backend.customers.usecases.iam.get_token.infrastructure.rest;

public record GetTokenRequest(
        String email,
        String password
) {
}

