package es.maryshopping.backend.catalog.usecases.create_category.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.create_category.application.CreateCategoryCommand;
import es.maryshopping.backend.catalog.usecases.create_category.application.CreateCategoryResult;
import es.maryshopping.backend.catalog.usecases.create_category.application.CreateCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog/categories")
public class CreateCategoryController {

    private final CreateCategoryService createCategoryService;

    public CreateCategoryController(CreateCategoryService createCategoryService) {
        this.createCategoryService = createCategoryService;
    }

    @PostMapping
    public ResponseEntity<CreateCategoryResponse> proceed(@RequestBody CreateCategoryRequest request) {
        CreateCategoryCommand command = mapFromRequestToCommand(request);
        CreateCategoryResult result = createCategoryService.proceed(command);
        CreateCategoryResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private CreateCategoryCommand mapFromRequestToCommand(CreateCategoryRequest request) {
        return CreateCategoryCommand.builder()
                .name(request.name())
                .build();
    }

    private CreateCategoryResponse mapFromResultToResponse(CreateCategoryResult result) {
        return CreateCategoryResponse.builder().id(result.id()).build();
    }
}
