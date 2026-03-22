package es.maryshopping.backend.orders.usecases.get_order.application;

import es.maryshopping.backend.orders.shared.domain.order.*;
import es.maryshopping.backend.orders.shared.domain.order_line.*;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineSpringRepository;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderSpringRepository;
import es.maryshopping.backend.shared_kernel.domain.address.*;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerQuery;
import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerResult;
import es.maryshopping.backend.shared_kernel.internal_api.customer.infrastructure.internal.GetCustomer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class GetOrderService {

    private final OrderSpringRepository orderRepository;
    private final OrderLineSpringRepository orderLineRepository;
    private final GetCustomer getCustomer;

    public GetOrderService(OrderSpringRepository orderRepository, OrderLineSpringRepository orderLineRepository, GetCustomer getCustomer) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.getCustomer = getCustomer;
    }


    public GetOrderResult proceed(GetOrderQuery query) {
        Optional<OrderEntity> optionalOrderEntity = orderRepository.findById(query.orderId());
        if (optionalOrderEntity.isEmpty()) {
            throw new EntityNotFoundException("Order not found");
        }
        OrderEntity orderEntity = optionalOrderEntity.get();
        List<OrderLineEntity> orderLineEntities = orderLineRepository.getAllByOrderId(query.orderId());
        GetCustomerResult customerResult = getCustomer.proceed(new GetCustomerQuery(orderEntity.getCustomerId()));

        // Construir Order de dominio para acceder al método total()
        Order order = buildOrderFromEntities(orderEntity, orderLineEntities);

        return GetOrderResult.builder()
                .orderId(orderEntity.getId())
                .orderDate(orderEntity.getOrderDate())
                .customer(GetOrderResult.CustomerResult.builder()
                        .customerId(customerResult.id())
                        .firstName(customerResult.firstName())
                        .lastName(customerResult.lastName())
                        .dni(customerResult.dni())
                        .build())
                .shippingAddress(GetOrderResult.AddressResult.builder()
                        .street(customerResult.shippingAddressStreet())
                        .city(customerResult.shippingAddressCity())
                        .province(customerResult.shippingAddressProvince())
                        .postalCode(customerResult.shippingAddressPostalCode())
                        .country(customerResult.shippingAddressCountry())
                        .build())
                .orderLines(fromOrderLineEntitiesToOrderLineResults(orderLineEntities))
                .totalAmountDue(order.total().value())
                .status(orderEntity.getStatus())
                .build();
    }

    private List<GetOrderResult.OrderLineResult> fromOrderLineEntitiesToOrderLineResults(List<OrderLineEntity> orderLineEntities) {
        return orderLineEntities.stream()
                .map(this::mapSingleOrderLineToResult)
                .toList();
    }

    private GetOrderResult.OrderLineResult mapSingleOrderLineToResult(OrderLineEntity entity) {
        BigDecimal subTotal = entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));
        return GetOrderResult.OrderLineResult.builder()
                .orderLineId(entity.getOrderLineId())
                .orderId(entity.getOrderId())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .productProviderReference(entity.getProductProviderReference())
                .categoryName(entity.getCategoryName())
                .providerName(entity.getProviderName())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .subTotal(subTotal)
                .build();
    }

    private Order buildOrderFromEntities(OrderEntity orderEntity, List<OrderLineEntity> orderLineEntities) {
        List<OrderLine> orderLines = orderLineEntities.stream()
                .map(this::mapFromEntityToOrderLine)
                .toList();

        return Order.builder()
                .id(new OrderId(orderEntity.getId()))
                .orderDate(new OrderDate(orderEntity.getOrderDate()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .shippingAddress(Address.builder()
                        .street(new Street(orderEntity.getShippingStreet()))
                        .city(new City(orderEntity.getShippingCity()))
                        .province(new Province(orderEntity.getShippingProvince()))
                        .postalCode(new PostalCode(orderEntity.getShippingPostalCode()))
                        .country(new Country(orderEntity.getShippingCountry()))
                        .build())
                .stock(new Stock(1)) // Stock dummy, no usado en GET
                .lines(orderLines)
                .build();
    }

    private OrderLine mapFromEntityToOrderLine(OrderLineEntity entity) {
        return OrderLine.builder()
                .id(new OrderLineId(entity.getOrderLineId()))
                .orderId(new OrderId(entity.getOrderId()))
                .categoryName(new CategoryName(entity.getCategoryName()))
                .productId(new ProductId(entity.getProductId()))
                .productName(new ProductName(entity.getProductName()))
                .providerName(new ProviderName(entity.getProviderName()))
                .productProviderReference(new ProductProviderReference(entity.getProductProviderReference()))
                .quantity(new Quantity(entity.getQuantity()))
                .unitPrice(new UnitPrice(entity.getUnitPrice()))
                .build();
    }
}
