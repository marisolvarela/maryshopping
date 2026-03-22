package es.maryshopping.backend.customers.usecases.iam.logout.infrastructure.rest;

import es.maryshopping.backend.customers.usecases.iam.logout.application.LogoutCommand;
import es.maryshopping.backend.customers.usecases.iam.logout.application.LogoutResult;
import es.maryshopping.backend.customers.usecases.iam.logout.application.LogoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers/iam")
public class LogoutController {

    private final LogoutService logoutService;

    public LogoutController(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> proceed(@RequestBody LogoutRequest request) {
        LogoutCommand command = mapFromRequestToCommand(request);
        LogoutResult result = logoutService.proceed(command);
        LogoutResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private LogoutCommand mapFromRequestToCommand(LogoutRequest request) {
        return new LogoutCommand(request.refreshToken(), request.accessToken());
    }

    private LogoutResponse mapFromResultToResponse(LogoutResult result) {
        return new LogoutResponse(result.success(), result.message());
    }
}

