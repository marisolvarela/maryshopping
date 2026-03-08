package es.maryshopping.backend.catalog.usecases.get_all_categories.infrastrucutre.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CategoryResponse(
        UUID id,
        String name

) {
}
