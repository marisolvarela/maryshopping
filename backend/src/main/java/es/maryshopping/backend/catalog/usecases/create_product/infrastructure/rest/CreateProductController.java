package es.maryshopping.backend.catalog.usecases.create_product.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.create_product.application.CreateProductCommand;
import es.maryshopping.backend.catalog.usecases.create_product.application.CreateProductResult;
import es.maryshopping.backend.catalog.usecases.create_product.application.CreateProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog/products")
public class CreateProductController {
    private final CreateProductService createProductService;

    public CreateProductController(CreateProductService createProductService) {
        this.createProductService = createProductService;
    }

    @PostMapping
    public ResponseEntity<CreateProductResponse> proceed(@RequestBody CreateProductRequest request) {
        CreateProductCommand command = mapFromRequestToCommand(request);
        CreateProductResult result = createProductService.proceed(command);
        CreateProductResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);

    }

    private CreateProductCommand mapFromRequestToCommand(CreateProductRequest request) {
        return CreateProductCommand.builder()
                .name(request.name())
                .categoryId(request.categoryId())
                .description(request.description())
                .providerId(request.providerId())
                .providerReference(request.providerReference())
                .price(request.price())
                .build();
    }

    private CreateProductResponse mapFromResultToResponse(CreateProductResult result) {
        return CreateProductResponse.builder().productId(result.productId()).build();

    }
}
