package es.maryshopping.backend.catalog.usecases.update_product.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.update_product.application.UpdateProductCommand;
import es.maryshopping.backend.catalog.usecases.update_product.application.UpdateProductResult;
import es.maryshopping.backend.catalog.usecases.update_product.application.UpdateProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/products")
public class UpdateProductController {
    private final UpdateProductService updateProductService;

    public UpdateProductController(UpdateProductService updateProductService) {
        this.updateProductService = updateProductService;
    }

    @PutMapping("/{productId}")
    public ResponseEntity<UpdateProductResponse> proceed(@PathVariable UUID productId, @RequestBody UpdateProductRequest request) {
        UpdateProductCommand command = mapFromRequestToCommand(productId, request);
        UpdateProductResult result = updateProductService.proceed(command);
        UpdateProductResponse response = mapFromResultToResponse((result));
        return ResponseEntity.status(200).body(response);
    }

    private UpdateProductCommand mapFromRequestToCommand(UUID productId, UpdateProductRequest request) {
        return UpdateProductCommand.builder()
                .id(productId)
                .name(request.name())
                .categoryId(request.categoryId())
                .description(request.description())
                .provider(request.provider())
                .providerReference(request.providerReference())
                .price(request.price())
                .build();
    }

    private UpdateProductResponse mapFromResultToResponse(UpdateProductResult result) {
        return UpdateProductResponse.builder().id(result.id()).build();
    }
}
