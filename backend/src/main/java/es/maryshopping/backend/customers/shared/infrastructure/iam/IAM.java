package es.maryshopping.backend.customers.shared.infrastructure.iam;

import jakarta.ws.rs.core.Response;

public interface IAM {
    TokenIamResponse authenticateUser(String email, String password);

    LogoutIamResponse logoutUser(String refreshToken);

    String createUser(String username, String email, String firstName, String lastName,
                      String password, String roleName);

    void updateUser(String userId, String username, String email,
                    String firstName, String lastName, String password);

    void deleteUser(String userId);

    void assignRealmRole(String userId, String roleName);

    void validateRealmConfiguration();

    String extractUserIdFromLocationHeader(Response response);
}
