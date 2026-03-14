package es.maryshopping.backend.catalog.usecases.create_provider.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.create_provider.application.CreateProviderCommand;
import es.maryshopping.backend.catalog.usecases.create_provider.application.CreateProviderResult;
import es.maryshopping.backend.catalog.usecases.create_provider.application.CreateProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("catalog/products/providers")
public class CreateProviderController {
private final CreateProviderService createProviderService;

    public CreateProviderController(CreateProviderService createProviderService) {
        this.createProviderService = createProviderService;
    }
    @PostMapping
    public ResponseEntity<CreateProviderResponse>proceed(@RequestBody CreateProviderRequest request){
        CreateProviderCommand command = mapFromRequestToCommand(request);
        CreateProviderResult result = createProviderService.proceed(command);
        CreateProviderResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }
    private CreateProviderCommand mapFromRequestToCommand(CreateProviderRequest request){
        return CreateProviderCommand.builder()
                .name(request.name())
                .build();
    }
    private CreateProviderResponse mapFromResultToResponse(CreateProviderResult result){
        return CreateProviderResponse.builder().id(result.id()).build();
    }
}
