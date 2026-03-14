package es.maryshopping.backend.catalog.usecases.get_all_providers.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProviderEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProviderSpringRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllProvidersService {
    private final ProviderSpringRepository repository;

    public GetAllProvidersService(ProviderSpringRepository repository) {
        this.repository = repository;
    }

    public List<ProviderResult> proceed(GetAllProvidersQuery query) {
        List<ProviderEntity> providerEntities = repository.findAll();
        return providerEntities.stream()
                .map(this::fromEntityToResult)
                .toList();
    }

    private ProviderResult fromEntityToResult(ProviderEntity entity) {
        return ProviderResult.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
