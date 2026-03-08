package es.maryshopping.backend.catalog.usecases.delete_category.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.delete_category.application.DeleteCategoryCommand;
import es.maryshopping.backend.catalog.usecases.delete_category.application.DeleteCategoryResult;
import es.maryshopping.backend.catalog.usecases.delete_category.application.DeleteCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/categories")
public class DeleteCategoryController {
    private final DeleteCategoryService deleteCategoryService;

    public DeleteCategoryController(DeleteCategoryService deleteCategoryService) {
        this.deleteCategoryService = deleteCategoryService;
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<DeleteCategoryResponse> proceed(@PathVariable("categoryId") UUID categoryId){
        DeleteCategoryCommand command = mapFromPathToCommand(categoryId);
        DeleteCategoryResult result = deleteCategoryService.proceed(command);
        DeleteCategoryResponse response=mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private DeleteCategoryCommand mapFromPathToCommand(UUID categoryId){
        return DeleteCategoryCommand.builder()
                .id(categoryId)
                .build();
    }

    private DeleteCategoryResponse mapFromResultToResponse(DeleteCategoryResult result){
        return DeleteCategoryResponse.builder().id(result.id()).build();
    }
}
