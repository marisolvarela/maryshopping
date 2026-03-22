package es.maryshopping.backend.customers.shared.infrastructure.iam;

public record TokenIamResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}

