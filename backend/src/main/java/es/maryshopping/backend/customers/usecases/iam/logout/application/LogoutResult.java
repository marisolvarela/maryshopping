package es.maryshopping.backend.customers.usecases.iam.logout.application;

public record LogoutResult(
        boolean success,
        String message
) {
}

