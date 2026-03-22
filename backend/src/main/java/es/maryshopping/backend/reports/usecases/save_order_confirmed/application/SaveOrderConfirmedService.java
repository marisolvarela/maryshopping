package es.maryshopping.backend.reports.usecases.save_order_confirmed.application;

import es.maryshopping.backend.reports.shared.infrastructure.persistence.OrderReportEntity;
import es.maryshopping.backend.reports.shared.infrastructure.persistence.OrderReportSpringRepository;
import es.maryshopping.backend.shared_kernel.internal_api.reports.application.EventResult;
import es.maryshopping.backend.shared_kernel.internal_api.reports.application.OrderConfirmedEvent;
import es.maryshopping.backend.shared_kernel.internal_api.reports.application.OrderConfirmedResult;
import es.maryshopping.backend.shared_kernel.internal_api.reports.infrastructure.internal.OrderConfirmed;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaveOrderConfirmedService implements OrderConfirmed {

    private final OrderReportSpringRepository repository;

    public SaveOrderConfirmedService(OrderReportSpringRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrderConfirmedResult proceed(OrderConfirmedEvent event) {
        List<OrderReportEntity> entities = event.orderLineEvents().stream()
                .map(orderLineEvent -> mapToEntity(event, orderLineEvent))
                .toList();
        repository.saveAll(entities);
        return OrderConfirmedResult.builder().result(EventResult.SUCCESS).build();
    }

    private OrderReportEntity mapToEntity(OrderConfirmedEvent event, OrderConfirmedEvent.OrderLineEvent line) {
        OrderReportEntity entity = new OrderReportEntity();

        entity.setOrderId(event.orderId());
        entity.setOrderDate(event.orderDate());

        entity.setCustomerId(event.customerId());
        entity.setCustomerFirstName(event.customerFirstName());
        entity.setCustomerLastName(event.customerLastName());
        entity.setCustomerDni(event.customerDni());

        entity.setShippingStreet(event.shippingStreet());
        entity.setShippingCity(event.shippingCity());
        entity.setShippingProvince(event.shippingProvince());
        entity.setShippingPostalCode(event.shippingPostalCode());
        entity.setShippingCountry(event.shippingCountry());

        entity.setOrderLineId(line.orderLineId());
        entity.setProductId(line.productId());
        entity.setProductName(line.productName());
        entity.setProductProviderReference(line.productProviderReference());
        entity.setCategoryName(line.categoryName());
        entity.setProviderName(line.providerName());
        entity.setQuantity(line.quantity());
        entity.setUnitPrice(line.unitPrice());
        entity.setSubTotal(line.subTotal());

        entity.setTotalAmountDue(event.totalAmountDue());

        return entity;
    }
}

