package es.maryshopping.backend.catalog.shared.domain.product;

import es.maryshopping.backend.catalog.shared.domain.category.Category;
import es.maryshopping.backend.catalog.shared.domain.product.image.Image;
import es.maryshopping.backend.catalog.shared.domain.provider.Provider;
import lombok.Builder;

import java.util.List;

@Builder
public record Product(
        ProductId id,
        ProductName name,
        Category category,
        ProductDescription description,
        Provider provider,
        ProductProviderReference productProviderReference,
        Price price,
        List<Image> images
) {
}
