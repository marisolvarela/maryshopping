package es.maryshopping.backend.catalog.shared.domain.product.image;

import es.maryshopping.backend.catalog.shared.domain.product.ProductId;
import lombok.Builder;

@Builder
public record Image(
        ImageId id,
        ImageBytes bytes,
        ImageMimeType mimeType,
        ImageOrder order,
        IsPrimary primary,
        ProductId productId
) {
}
