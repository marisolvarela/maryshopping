package es.maryshopping.backend.catalog.usecases.get_provider.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.get_provider.application.GetProviderQuery;
import es.maryshopping.backend.catalog.usecases.get_provider.application.GetProviderResult;
import es.maryshopping.backend.catalog.usecases.get_provider.application.GetProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/products/providers")
public class GetProviderController {
    private final GetProviderService getProviderService;

    public GetProviderController(GetProviderService getProviderService) {
        this.getProviderService = getProviderService;
    }
    @GetMapping("/{providerId}")
    public ResponseEntity<GetProviderResponse>proceed(@PathVariable UUID providerId){
        GetProviderQuery query = mapFromPathToQuery(providerId);
        GetProviderResult result = getProviderService.proceed(query);
        GetProviderResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);

    }
    private GetProviderQuery mapFromPathToQuery(UUID providerId){
        return  new GetProviderQuery(providerId);
    }
    private GetProviderResponse mapFromResultToResponse(GetProviderResult result){
        return GetProviderResponse.builder().id(result.id()).name(result.name()).build();
    }
}
