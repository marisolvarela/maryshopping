package es.maryshopping.backend.catalog.shared.domain.category;

import lombok.Builder;

@Builder
public record Category(
        CategoryId id,
        CategoryName name
) {
}
