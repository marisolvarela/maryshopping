package es.maryshopping.backend.customers.usecases.iam.get_token.application;

public record GetTokenCommand(
        String email,
        String password
) {
}

