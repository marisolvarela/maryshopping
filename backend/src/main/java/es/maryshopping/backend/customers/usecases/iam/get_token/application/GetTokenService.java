package es.maryshopping.backend.customers.usecases.iam.get_token.application;


import es.maryshopping.backend.customers.shared.infrastructure.iam.IAM;
import es.maryshopping.backend.customers.shared.infrastructure.iam.TokenIamResponse;
import org.springframework.stereotype.Service;

@Service
public class GetTokenService {

    private final IAM iam;

    public GetTokenService(IAM iam) {
        this.iam = iam;
    }

    public GetTokenResult proceed(GetTokenCommand command) {
        TokenIamResponse tokenIamResponse = authenticateInIam(command);
        return mapFromTokenResponseToResult(tokenIamResponse);
    }

    private TokenIamResponse authenticateInIam(GetTokenCommand command) {
        return iam.authenticateUser(command.email(), command.password());
    }

    private GetTokenResult mapFromTokenResponseToResult(TokenIamResponse tokenIamResponse) {
        return new GetTokenResult(
                tokenIamResponse.accessToken(),
                tokenIamResponse.refreshToken(),
                tokenIamResponse.tokenType(),
                tokenIamResponse.expiresIn()
        );
    }
}

