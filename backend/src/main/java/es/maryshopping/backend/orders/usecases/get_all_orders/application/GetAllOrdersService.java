package es.maryshopping.backend.orders.usecases.get_all_orders.application;

import es.maryshopping.backend.orders.shared.domain.order.*;
import es.maryshopping.backend.orders.shared.domain.order_line.*;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineSpringRepository;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderSpringRepository;
import es.maryshopping.backend.shared_kernel.domain.address.*;
import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerQuery;
import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerResult;
import es.maryshopping.backend.shared_kernel.internal_api.customer.infrastructure.internal.GetCustomer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetAllOrdersService {

    private final OrderSpringRepository orderRepository;
    private final OrderLineSpringRepository orderLineRepository;
    private final GetCustomer getCustomer;

    public GetAllOrdersService(
            OrderSpringRepository orderRepository,
            OrderLineSpringRepository orderLineRepository,
            GetCustomer getCustomer) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.getCustomer = getCustomer;
    }

    public GetAllOrdersResult proceed(GetAllOrdersQuery query) {
        // 1. Obtener todas las órdenes
        List<OrderEntity> orderEntities = orderRepository.findAll();

        // 2. Obtener todas las líneas de órdenes (optimizado: una sola query)
        List<OrderLineEntity> allOrderLineEntities = orderLineRepository.findAll();

        // 3. Agrupar líneas por orden para acceso rápido
        Map<java.util.UUID, List<OrderLineEntity>> linesByOrderId = allOrderLineEntities.stream()
                .collect(Collectors.groupingBy(OrderLineEntity::getOrderId));

        // 4. Mapear órdenes a resultado
        List<GetAllOrdersResult.OrderResult> orders = orderEntities.stream()
                .map(orderEntity -> mapOrderEntityToResult(
                        orderEntity,
                        linesByOrderId.getOrDefault(orderEntity.getId(), List.of())))
                .toList();

        return GetAllOrdersResult.builder()
                .orders(orders)
                .build();
    }

    private GetAllOrdersResult.OrderResult mapOrderEntityToResult(
            OrderEntity orderEntity,
            List<OrderLineEntity> orderLineEntities) {

        // Obtener datos del cliente
        GetCustomerResult customerResult = getCustomer.proceed(
                new GetCustomerQuery(orderEntity.getCustomerId()));

        // Construir Order de dominio para calcular total
        Order order = buildOrderFromEntities(orderEntity, orderLineEntities);

        return GetAllOrdersResult.OrderResult.builder()
                .orderId(orderEntity.getId())
                .orderDate(orderEntity.getOrderDate())
                .customerId(customerResult.id())
                .firstName(customerResult.firstName())
                .lastName(customerResult.lastName())
                .dni(customerResult.dni())
                .shippingAddress(GetAllOrdersResult.OrderResult.AddressResult.builder()
                        .street(orderEntity.getShippingStreet())
                        .city(orderEntity.getShippingCity())
                        .province(orderEntity.getShippingProvince())
                        .postalCode(orderEntity.getShippingPostalCode())
                        .country(orderEntity.getShippingCountry())
                        .build())
                .orderLines(mapOrderLineEntitiesToResults(orderLineEntities))
                .totalAmountDue(order.total().value())
                .status(orderEntity.getStatus())
                .build();
    }

    private List<GetAllOrdersResult.OrderResult.OrderLineResult> mapOrderLineEntitiesToResults(
            List<OrderLineEntity> orderLineEntities) {
        return orderLineEntities.stream()
                .map(this::mapSingleOrderLineToResult)
                .toList();
    }

    private GetAllOrdersResult.OrderResult.OrderLineResult mapSingleOrderLineToResult(
            OrderLineEntity entity) {
        BigDecimal subTotal = entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));
        return GetAllOrdersResult.OrderResult.OrderLineResult.builder()
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
                .stock(new Stock(1))
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

