package es.maryshopping.backend.catalog.usecases.delete_provider.application;

import es.maryshopping.backend.catalog.shared.infrastructure.persistence.ProviderSpringRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteProviderService {
    private final ProviderSpringRepository repository;

    public DeleteProviderService(ProviderSpringRepository repository) {
        this.repository = repository;
    }

   public DeleteProviderResult proceed(DeleteProviderCommand command){
        repository.deleteById(command.id());
        return DeleteProviderResult.builder().id(command.id()).build();
   }
}
