package es.maryshopping.backend.catalog.usecases.get_provider.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProviderEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProviderSpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetProviderService {
    private final ProviderSpringRepository repository;

    public GetProviderService(ProviderSpringRepository repository) {
        this.repository = repository;
    }

    public GetProviderResult proceed(GetProviderQuery query) {
        Optional<ProviderEntity> optionalProviderEntity = repository.findById(query.id());
        if (optionalProviderEntity.isEmpty()) {
            throw new EntityNotFoundException("Provider not found");
        }
        ProviderEntity entity = optionalProviderEntity.get();
        return GetProviderResult.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
