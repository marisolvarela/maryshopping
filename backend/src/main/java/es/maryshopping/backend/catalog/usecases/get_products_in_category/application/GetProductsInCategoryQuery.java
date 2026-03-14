package es.maryshopping.backend.catalog.usecases.get_products_in_category.application;

import java.util.UUID;

public record GetProductsInCategoryQuery(
        UUID categoryId
) {
}

