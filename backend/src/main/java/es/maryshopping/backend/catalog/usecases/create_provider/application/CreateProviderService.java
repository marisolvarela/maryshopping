package es.maryshopping.backend.catalog.usecases.create_provider.application;

import es.maryshopping.backend.catalog.shared.domain.provider.Provider;
import es.maryshopping.backend.catalog.shared.domain.provider.ProviderId;
import es.maryshopping.backend.catalog.shared.domain.provider.ProviderName;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProviderEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProviderSpringRepository;
import es.maryshopping.backend.shared_kernel.exceptions.DuplicatedEntityException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
@Service
public class CreateProviderService {
    private final ProviderSpringRepository repository;

    public CreateProviderService(ProviderSpringRepository repository) {
        this.repository = repository;
    }

    public CreateProviderResult proceed(CreateProviderCommand command) {
        Optional<ProviderEntity> optionalByName = repository.findByName(command.name());
        if (optionalByName.isPresent()) {
            throw new DuplicatedEntityException("A provider with that name already exits");
        }
        UUID providerId = UUID.randomUUID();
        Provider provider = mapFromCommandToDomain(providerId, command);
        ProviderEntity entity = ProviderEntity.fromDomain(provider);
        ProviderEntity saved = repository.save(entity);
        return CreateProviderResult.builder().id(saved.getId()).build();

    }

    private Provider mapFromCommandToDomain(UUID providerId, CreateProviderCommand command) {
        return Provider.builder()
                .id(new ProviderId(providerId))
                .name(new ProviderName(command.name()))
                .build();
    }

}
