package es.maryshopping.backend.customers.usecases.iam.logout.application;


import es.maryshopping.backend.customers.shared.infrastructure.iam.IAM;
import es.maryshopping.backend.customers.shared.infrastructure.iam.LogoutIamResponse;
import es.maryshopping.backend.shared_kernel.security.TokenBlacklistService;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    private final IAM iam;
    private final TokenBlacklistService tokenBlacklistService;

    public LogoutService(IAM iam, TokenBlacklistService tokenBlacklistService) {
        this.iam = iam;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public LogoutResult proceed(LogoutCommand command) {
        // Step 1: blacklist the access token so it is rejected immediately
        blacklistAccessToken(command.accessToken());

        // Step 2: end the session in Keycloak (invalidates refresh token + session)
        LogoutIamResponse logoutResponse = logoutFromIam(command);

        return mapFromLogoutResponseToResult(logoutResponse);
    }

    private void blacklistAccessToken(String accessToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            tokenBlacklistService.blacklist(accessToken);
        }
    }

    private LogoutIamResponse logoutFromIam(LogoutCommand command) {
        return iam.logoutUser(command.refreshToken());
    }

    private LogoutResult mapFromLogoutResponseToResult(LogoutIamResponse logoutResponse) {
        return new LogoutResult(logoutResponse.success(), logoutResponse.message());
    }
}

