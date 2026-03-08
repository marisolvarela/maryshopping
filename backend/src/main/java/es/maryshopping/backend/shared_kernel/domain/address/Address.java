package es.maryshopping.backend.shared_kernel.domain.address;

import lombok.Builder;

@Builder
public record Address(
        City city,
        Country country,
        PostalCode postalCode,
        Province province,
        Street street
) {
}
