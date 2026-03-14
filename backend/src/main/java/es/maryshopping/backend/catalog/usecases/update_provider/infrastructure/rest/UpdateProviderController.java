package es.maryshopping.backend.catalog.usecases.update_provider.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.update_provider.application.UpdateProviderCommand;
import es.maryshopping.backend.catalog.usecases.update_provider.application.UpdateProviderResult;
import es.maryshopping.backend.catalog.usecases.update_provider.application.UpdateProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/products/providers")
public class UpdateProviderController {
    private final UpdateProviderService updateProviderService;

    public UpdateProviderController(UpdateProviderService updateProviderService) {
        this.updateProviderService = updateProviderService;
    }

    @PutMapping("/{providerId}")
    public ResponseEntity<UpdateProviderResponse> proceed(@PathVariable UUID providerId, @RequestBody UpdateProviderRequest request) {
        UpdateProviderCommand command = mapFromRequestToCommand(providerId, request);
        UpdateProviderResult result = updateProviderService.proceed(command);
        UpdateProviderResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private UpdateProviderCommand mapFromRequestToCommand(UUID providerId, UpdateProviderRequest request) {
        return UpdateProviderCommand.builder()
                .id(providerId)
                .name(request.name())
                .build();
    }

    private UpdateProviderResponse mapFromResultToResponse(UpdateProviderResult result) {
        return UpdateProviderResponse.builder().id(result.id()).build();
    }
}