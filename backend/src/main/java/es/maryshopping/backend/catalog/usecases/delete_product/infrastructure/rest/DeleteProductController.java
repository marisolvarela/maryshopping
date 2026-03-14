package es.maryshopping.backend.catalog.usecases.delete_product.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.delete_product.application.DeleteProductCommand;
import es.maryshopping.backend.catalog.usecases.delete_product.application.DeleteProductResult;
import es.maryshopping.backend.catalog.usecases.delete_product.application.DeleteProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/products")
public class DeleteProductController {
    private final DeleteProductService deleteProductService;

    public DeleteProductController(DeleteProductService deleteProductService) {
        this.deleteProductService = deleteProductService;
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<DeleteProductResponse> proceed(@PathVariable UUID productId) {
        DeleteProductCommand command = mapFromPathVariableToCommand(productId);
        DeleteProductResult result = deleteProductService.proceed(command);
        DeleteProductResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private DeleteProductCommand mapFromPathVariableToCommand(UUID productId) {
        return DeleteProductCommand.builder()
                .id(productId)
                .build();
    }

    private DeleteProductResponse mapFromResultToResponse(DeleteProductResult result) {
        return DeleteProductResponse.builder().id(result.id()).build();
    }
}
