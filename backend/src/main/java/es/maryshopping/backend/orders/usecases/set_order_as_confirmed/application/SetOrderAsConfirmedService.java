package es.maryshopping.backend.orders.usecases.set_order_as_confirmed.application;

import es.maryshopping.backend.orders.shared.domain.order.*;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineEntity;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderLineSpringRepository;
import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderSpringRepository;
import es.maryshopping.backend.shared_kernel.domain.address.*;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerQuery;
import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerResult;
import es.maryshopping.backend.shared_kernel.internal_api.customer.infrastructure.internal.GetCustomer;
import es.maryshopping.backend.shared_kernel.internal_api.reports.application.OrderConfirmedEvent;
import es.maryshopping.backend.shared_kernel.internal_api.reports.infrastructure.internal.OrderConfirmed;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class SetOrderAsConfirmedService {

    private final OrderSpringRepository orderRepository;
    private final OrderLineSpringRepository orderLineRepository;
    private final GetCustomer getCustomer;
    private final OrderConfirmed orderConfirmed;

    public SetOrderAsConfirmedService(OrderSpringRepository orderRepository,
                                      OrderLineSpringRepository orderLineRepository,
                                      GetCustomer getCustomer,
                                      OrderConfirmed orderConfirmed) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.getCustomer = getCustomer;
        this.orderConfirmed = orderConfirmed;
    }

    public SetOrderAsConfirmedResult proceed(SetOrderAsConfirmedCommand command) {
        // Step 1: confirm the order and persist it
        OrderEntity confirmedOrder = confirmAndSaveOrder(command);

        // Step 2: emit event to reports module
        emitOrderConfirmedEvent(confirmedOrder);

        return SetOrderAsConfirmedResult.builder().id(confirmedOrder.getId()).build();
    }

    // ─── Step 1 ───────────────────────────────────────────────────────────────

    private OrderEntity confirmAndSaveOrder(SetOrderAsConfirmedCommand command) {
        OrderEntity currentEntity = findOrderOrThrow(command.id());
        Order confirmedOrder = buildConfirmedOrder(command.id(), currentEntity);
        return orderRepository.save(OrderEntity.fromDomain(confirmedOrder));
    }

    private OrderEntity findOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    private Order buildConfirmedOrder(UUID orderId, OrderEntity current) {
        return Order.builder()
                .id(new OrderId(orderId))
                .orderDate(new OrderDate(current.getOrderDate()))
                .customerId(new CustomerId(current.getCustomerId()))
                .shippingAddress(buildShippingAddress(current))
                .stock(new Stock(35))
                .status(new Status(StatusEnum.CONFIRMED))
                .build();
    }

    private Address buildShippingAddress(OrderEntity current) {
        return Address.builder()
                .street(new Street(current.getShippingStreet()))
                .city(new City(current.getShippingCity()))
                .province(new Province(current.getShippingProvince()))
                .postalCode(new PostalCode(current.getShippingPostalCode()))
                .country(new Country(current.getShippingCountry()))
                .build();
    }

    // ─── Step 2 ───────────────────────────────────────────────────────────────

    private void emitOrderConfirmedEvent(OrderEntity confirmedOrder) {
        List<OrderLineEntity> orderLines = fetchOrderLines(confirmedOrder.getId());
        GetCustomerResult customer = fetchCustomer(confirmedOrder.getCustomerId());
        OrderConfirmedEvent event = buildOrderConfirmedEvent(confirmedOrder, orderLines, customer);
        orderConfirmed.proceed(event);
    }

    private List<OrderLineEntity> fetchOrderLines(UUID orderId) {
        return orderLineRepository.getAllByOrderId(orderId);
    }

    private GetCustomerResult fetchCustomer(UUID customerId) {
        return getCustomer.proceed(new GetCustomerQuery(customerId));
    }

    private OrderConfirmedEvent buildOrderConfirmedEvent(OrderEntity order,
                                                         List<OrderLineEntity> orderLines,
                                                         GetCustomerResult customer) {
        List<OrderConfirmedEvent.OrderLineEvent> lineEvents = buildLineEvents(orderLines);
        BigDecimal totalAmountDue = calculateTotal(lineEvents);

        return new OrderConfirmedEvent(
                order.getId(),
                order.getOrderDate(),
                order.getCustomerId(),
                customer.firstName(),
                customer.lastName(),
                customer.dni(),
                order.getShippingStreet(),
                order.getShippingCity(),
                order.getShippingProvince(),
                order.getShippingPostalCode(),
                order.getShippingCountry(),
                totalAmountDue,
                lineEvents
        );
    }

    private List<OrderConfirmedEvent.OrderLineEvent> buildLineEvents(List<OrderLineEntity> orderLines) {
        return orderLines.stream()
                .map(this::buildLineEvent)
                .toList();
    }

    private OrderConfirmedEvent.OrderLineEvent buildLineEvent(OrderLineEntity line) {
        BigDecimal subTotal = line.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
        return new OrderConfirmedEvent.OrderLineEvent(
                line.getOrderLineId(),
                line.getProductId(),
                line.getProductName(),
                line.getProductProviderReference(),
                line.getQuantity(),
                line.getUnitPrice(),
                subTotal,
                line.getCategoryName(),
                line.getProviderName()
        );
    }

    private BigDecimal calculateTotal(List<OrderConfirmedEvent.OrderLineEvent> lineEvents) {
        return lineEvents.stream()
                .map(OrderConfirmedEvent.OrderLineEvent::subTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
