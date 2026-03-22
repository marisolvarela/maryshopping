package es.maryshopping.backend.reports.usecases.get_top_customers.infrastructure.rest;

import es.maryshopping.backend.reports.usecases.get_top_customers.application.GetTopCustomersQuery;
import es.maryshopping.backend.reports.usecases.get_top_customers.application.GetTopCustomersResult;
import es.maryshopping.backend.reports.usecases.get_top_customers.application.GetTopCustomersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports/top-customers")
public class GetTopCustomersController {

    private final GetTopCustomersService service;

    public GetTopCustomersController(GetTopCustomersService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<GetTopCustomersResponse>> proceed(@RequestParam(required = false) Integer limit) {
        GetTopCustomersQuery query = GetTopCustomersQuery.builder().limit(limit).build();
        List<GetTopCustomersResult> result = service.proceed(query);
        return ResponseEntity.status(200).body(mapFromResultToResponse(result));
    }

    private List<GetTopCustomersResponse> mapFromResultToResponse(List<GetTopCustomersResult> results) {
        return results.stream()
                .map(r -> GetTopCustomersResponse.builder()
                        .customerId(r.customerId())
                        .firstName(r.firstName())
                        .lastName(r.lastName())
                        .dni(r.dni())
                        .totalSpent(r.totalSpent())
                        .build())
                .toList();
    }
}

