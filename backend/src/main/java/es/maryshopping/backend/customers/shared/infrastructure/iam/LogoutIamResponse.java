package es.maryshopping.backend.customers.shared.infrastructure.iam;

import lombok.Builder;

@Builder
public record LogoutIamResponse(boolean success, String message) {
}
