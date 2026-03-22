package es.maryshopping.backend.customers.usecases.iam.logout.application;

public record LogoutCommand(
        String refreshToken,
        String accessToken
) {
}

