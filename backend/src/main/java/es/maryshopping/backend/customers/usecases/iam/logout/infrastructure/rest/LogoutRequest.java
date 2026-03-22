package es.maryshopping.backend.customers.usecases.iam.logout.infrastructure.rest;

public record LogoutRequest(
        String refreshToken,
        String accessToken
) {
}

