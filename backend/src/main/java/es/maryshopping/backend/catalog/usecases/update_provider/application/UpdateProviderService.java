package es.maryshopping.backend.catalog.usecases.update_provider.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProviderEntity;
import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProviderSpringRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateProviderService {
    private final ProviderSpringRepository repository;

    public UpdateProviderService(ProviderSpringRepository repository) {
        this.repository = repository;
    }

    public UpdateProviderResult proceed(UpdateProviderCommand command){
        ProviderEntity entity = new ProviderEntity(command.id(), command.name());
        ProviderEntity saved = repository.save(entity);
        return UpdateProviderResult.builder().id(saved.getId()).build();
    }

}
