package es.maryshopping.backend.catalog.usecases.delete_provider.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.delete_provider.application.DeleteProviderCommand;
import es.maryshopping.backend.catalog.usecases.delete_provider.application.DeleteProviderResult;
import es.maryshopping.backend.catalog.usecases.delete_provider.application.DeleteProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/products/providers")
public class DeleteProviderController {
    private final DeleteProviderService deleteProviderService;

    public DeleteProviderController(DeleteProviderService deleteProviderService) {
        this.deleteProviderService = deleteProviderService;
    }
    @DeleteMapping("/{providerId}")
    public ResponseEntity<DeleteProviderResponse>proceed(@PathVariable UUID providerId){
        DeleteProviderCommand command = mapFromPathToCommand(providerId);
        DeleteProviderResult result = deleteProviderService.proceed(command);
        DeleteProviderResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);

    }
    private DeleteProviderCommand mapFromPathToCommand(UUID providerId){
        return DeleteProviderCommand.builder()
                .id(providerId)
                .build();
    }
    private DeleteProviderResponse mapFromResultToResponse(DeleteProviderResult result){
        return DeleteProviderResponse.builder().id(result.id()).build();
    }
}
