package es.maryshopping.backend.reports.usecases.get_top_customers.application;

import es.maryshopping.backend.reports.shared.infrastructure.persistence.OrderReportSpringRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetTopCustomersService {

    private final OrderReportSpringRepository repository;

    public GetTopCustomersService(OrderReportSpringRepository repository) {
        this.repository = repository;
    }

    public List<GetTopCustomersResult> proceed(GetTopCustomersQuery query) {
        Integer limit = query.limit() == null ? 10 : query.limit();
        return repository.getTopCustomers(limit);
    }
}

