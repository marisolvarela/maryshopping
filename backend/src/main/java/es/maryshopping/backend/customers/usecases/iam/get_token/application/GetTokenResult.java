package es.maryshopping.backend.customers.usecases.iam.get_token.application;

public record GetTokenResult(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}

