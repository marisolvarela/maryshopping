package es.maryshopping.backend.catalog.usecases.images.get_product_images.infrastrucutre.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ImageResponse(
        UUID id,
        UUID productId,
        String mimeType,
        Integer order,
        Boolean isPrimary
        //La imagen no se devuelve ,solo los metadatos de la misma.Una vez que el frontend tenga el id de la imagen
        // ,podrá hacer una petición a otro endpoint para obtener la imagen.
){
}
