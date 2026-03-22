package es.maryshopping.backend.reports.usecases.get_top_products.infrastructure.rest;

import es.maryshopping.backend.reports.usecases.get_top_products.application.GetTopProductsQuery;
import es.maryshopping.backend.reports.usecases.get_top_products.application.GetTopProductsResult;
import es.maryshopping.backend.reports.usecases.get_top_products.application.GetTopProductsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports/top-products")
public class GetTopProductsController {

    private final GetTopProductsService service;

    public GetTopProductsController(GetTopProductsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<GetTopProductsResponse>> proceed(@RequestParam(required = false) Integer limit) {
        GetTopProductsQuery query = GetTopProductsQuery.builder().limit(limit).build();
        List<GetTopProductsResult> result = service.proceed(query);
        return ResponseEntity.status(200).body(mapFromResultToResponse(result));
    }

    private List<GetTopProductsResponse> mapFromResultToResponse(List<GetTopProductsResult> results) {
        return results.stream()
                .map(r -> GetTopProductsResponse.builder()
                        .productId(r.productId())
                        .productName(r.productName())
                        .productProviderReference(r.productProviderReference())
                        .categoryName(r.categoryName())
                        .providerName(r.providerName())
                        .totalUnitsSold(r.totalUnitsSold())
                        .totalRevenue(r.totalRevenue())
                        .build())
                .toList();
    }
}

