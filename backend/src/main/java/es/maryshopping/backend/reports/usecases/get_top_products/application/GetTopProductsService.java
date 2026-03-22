package es.maryshopping.backend.reports.usecases.get_top_products.application;

import es.maryshopping.backend.reports.shared.infrastructure.persistence.OrderReportSpringRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetTopProductsService {

    private final OrderReportSpringRepository repository;

    public GetTopProductsService(OrderReportSpringRepository repository) {
        this.repository = repository;
    }

    public List<GetTopProductsResult> proceed(GetTopProductsQuery query) {
        Integer limit = query.limit() == null ? 10 : query.limit();
        return repository.getTopProducts(limit);
    }
}

