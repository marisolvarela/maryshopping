package es.maryshopping.backend.catalog.shared.infrastructure.persistence;

import es.maryshopping.backend.catalog.shared.domain.provider.Provider;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="provider", schema = "catalog")
public class ProviderEntity {
    @Id
    UUID id;
    String name;

    public static ProviderEntity fromDomain(Provider provider){
        return new ProviderEntity(
                provider.id().value(),
                provider.name().value()
        );
    }
}
