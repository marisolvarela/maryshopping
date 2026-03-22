package es.maryshopping.backend.customers.usecases.iam.get_token.infrastructure.rest;

import es.maryshopping.backend.customers.usecases.iam.get_token.application.GetTokenCommand;
import es.maryshopping.backend.customers.usecases.iam.get_token.application.GetTokenResult;
import es.maryshopping.backend.customers.usecases.iam.get_token.application.GetTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers/iam")
public class GetTokenController {

    private final GetTokenService getTokenService;

    public GetTokenController(GetTokenService getTokenService) {
        this.getTokenService = getTokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<GetTokenResponse> proceed(@RequestBody GetTokenRequest request) {
        GetTokenCommand command = mapFromRequestToCommand(request);
        GetTokenResult result = getTokenService.proceed(command);
        GetTokenResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private GetTokenCommand mapFromRequestToCommand(GetTokenRequest request) {
        return new GetTokenCommand(request.email(), request.password());
    }

    private GetTokenResponse mapFromResultToResponse(GetTokenResult result) {
        return new GetTokenResponse(
                result.accessToken(),
                result.refreshToken(),
                result.tokenType(),
                result.expiresIn()
        );
    }
}

