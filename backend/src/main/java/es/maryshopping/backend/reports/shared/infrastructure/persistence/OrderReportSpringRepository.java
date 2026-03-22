package es.maryshopping.backend.reports.shared.infrastructure.persistence;

import es.maryshopping.backend.reports.usecases.get_top_customers.application.GetTopCustomersResult;
import es.maryshopping.backend.reports.usecases.get_top_products.application.GetTopProductsResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrderReportSpringRepository extends JpaRepository<OrderReportEntity, UUID> {
    List<OrderReportEntity> findAllByOrderId(UUID orderId);

    @Query(value = """
            SELECT
                new es.maryshopping.backend.reports.usecases.get_top_products.application.GetTopProductsResult(
                    r.productId,
                    r.productName,
                    r.productProviderReference,
                    r.categoryName,
                    r.providerName,
                    SUM(r.quantity),
                    SUM(r.subTotal)
                )
            FROM OrderReportEntity r
            GROUP BY r.productId, r.productName, r.productProviderReference, r.categoryName, r.providerName
            ORDER BY SUM(r.quantity) DESC
            """)
    List<GetTopProductsResult> getTopProducts(org.springframework.data.domain.Pageable pageable);

    @Query(value = """
            SELECT
                new es.maryshopping.backend.reports.usecases.get_top_customers.application.GetTopCustomersResult(
                    r.customerId,
                    r.customerFirstName,
                    r.customerLastName,
                    r.customerDni,
                    SUM(r.subTotal)
                )
            FROM OrderReportEntity r
            GROUP BY r.customerId, r.customerFirstName, r.customerLastName, r.customerDni
            ORDER BY SUM(r.subTotal) DESC
            """)
    List<GetTopCustomersResult> getTopCustomers(org.springframework.data.domain.Pageable pageable);

    default List<GetTopProductsResult> getTopProducts(Integer limit) {
        return getTopProducts(org.springframework.data.domain.PageRequest.of(0, limit));
    }

    default List<GetTopCustomersResult> getTopCustomers(Integer limit) {
        return getTopCustomers(org.springframework.data.domain.PageRequest.of(0, limit));
    }
}
