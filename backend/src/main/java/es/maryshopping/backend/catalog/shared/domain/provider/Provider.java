package es.maryshopping.backend.catalog.shared.domain.provider;

import lombok.Builder;

@Builder
public record Provider(
        ProviderId id,
        ProviderName name
) {
}
