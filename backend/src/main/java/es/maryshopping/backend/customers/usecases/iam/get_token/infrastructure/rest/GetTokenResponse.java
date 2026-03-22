package es.maryshopping.backend.customers.usecases.iam.get_token.infrastructure.rest;

public record GetTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}

