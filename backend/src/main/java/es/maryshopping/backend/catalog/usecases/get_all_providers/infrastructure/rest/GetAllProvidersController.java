package es.maryshopping.backend.catalog.usecases.get_all_providers.infrastructure.rest;


import es.maryshopping.backend.catalog.usecases.get_all_providers.application.GetAllProvidersQuery;
import es.maryshopping.backend.catalog.usecases.get_all_providers.application.GetAllProvidersService;
import es.maryshopping.backend.catalog.usecases.get_all_providers.application.ProviderResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog/products/providers")
public class GetAllProvidersController {
    private final GetAllProvidersService GetAllProvidersService;

    public GetAllProvidersController(GetAllProvidersService GetAllProvidersService) {
        this.GetAllProvidersService = GetAllProvidersService;
    }

    @GetMapping
    public ResponseEntity<List<ProviderResponse>> proceed() {
        GetAllProvidersQuery query = createQuery();
        List<ProviderResult> providerResults = GetAllProvidersService.proceed(query);
        List<ProviderResponse> providerResponses = providerResults.stream()
                .map(this::mapFromResultToResponse)
                .toList();

        return ResponseEntity.status(200).body(providerResponses);
    }

    private GetAllProvidersQuery createQuery() {
        return new GetAllProvidersQuery();
    }

    private ProviderResponse mapFromResultToResponse(ProviderResult result) {
        return ProviderResponse.builder().id(result.id()).name(result.name()).build();
    }
}
