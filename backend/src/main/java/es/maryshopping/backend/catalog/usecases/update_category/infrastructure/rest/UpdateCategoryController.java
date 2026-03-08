package es.maryshopping.backend.catalog.usecases.update_category.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.update_category.application.UpdateCategoryCommand;
import es.maryshopping.backend.catalog.usecases.update_category.application.UpdateCategoryResult;
import es.maryshopping.backend.catalog.usecases.update_category.application.UpdateCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/categories")
public class UpdateCategoryController {
    private final UpdateCategoryService updateCategoryService;

    public UpdateCategoryController(UpdateCategoryService updateCategoryService) {
        this.updateCategoryService = updateCategoryService;
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<UpdateCategoryResponse> proceed(
            @PathVariable UUID categoryId,
            @RequestBody UpdateCategoryRequest request) {
        UpdateCategoryCommand command = mapFromRequestToCommand(categoryId, request);
        UpdateCategoryResult result = updateCategoryService.proceed(command);
        UpdateCategoryResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private UpdateCategoryCommand mapFromRequestToCommand(UUID categoryId, UpdateCategoryRequest request) {
        return UpdateCategoryCommand.builder()
                .id(categoryId)
                .name(request.name())
                .build();
    }

    private UpdateCategoryResponse mapFromResultToResponse(UpdateCategoryResult result) {
        return UpdateCategoryResponse.builder().id(result.id()).build();
    }
}
