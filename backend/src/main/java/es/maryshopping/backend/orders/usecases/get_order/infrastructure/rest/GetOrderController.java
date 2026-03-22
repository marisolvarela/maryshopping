package es.maryshopping.backend.orders.usecases.get_order.infrastructure.rest;

import es.maryshopping.backend.orders.shared.infrastructure.persistence.OrderOwnershipResolver;
import es.maryshopping.backend.orders.usecases.get_order.application.GetOrderQuery;
import es.maryshopping.backend.orders.usecases.get_order.application.GetOrderResult;
import es.maryshopping.backend.orders.usecases.get_order.application.GetOrderService;
import es.maryshopping.backend.shared_kernel.security.CustomerOwnershipValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class GetOrderController {

    private final GetOrderService getOrderService;
    private final OrderOwnershipResolver orderOwnershipResolver;
    private final CustomerOwnershipValidator customerOwnershipValidator;

    public GetOrderController(GetOrderService getOrderService,
                              OrderOwnershipResolver orderOwnershipResolver,
                              CustomerOwnershipValidator customerOwnershipValidator) {
        this.getOrderService = getOrderService;
        this.orderOwnershipResolver = orderOwnershipResolver;
        this.customerOwnershipValidator = customerOwnershipValidator;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<GetOrderResponse> proceed(@PathVariable UUID orderId) {
        UUID resourceCustomerId = orderOwnershipResolver.resolveCustomerIdByOrderId(orderId);
        customerOwnershipValidator.validate(resourceCustomerId);
        GetOrderQuery query = mapFromPathToQuery(orderId);
        GetOrderResult result = getOrderService.proceed(query);
        GetOrderResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private GetOrderQuery mapFromPathToQuery(UUID orderId) {
        return new GetOrderQuery(orderId);
    }

    private GetOrderResponse mapFromResultToResponse(GetOrderResult result) {
        return GetOrderResponse.builder()
                .orderId(result.orderId())
                .orderDate(result.orderDate())
                .customer(mapFromCustomerResultToCustomerResponse(result.customer()))
                .shippingAddress(mapFromAddressResultToAddressResponse(result.shippingAddress()))
                .orderLines(mapFromOrderLineResultsToOrderLineResponses(result.orderLines()))
                .totalAmountDue(result.totalAmountDue())
                .status(result.status())
                .build();
    }

    private GetOrderResponse.CustomerResponse mapFromCustomerResultToCustomerResponse(
            GetOrderResult.CustomerResult customerResult) {
        return GetOrderResponse.CustomerResponse.builder()
                .customerId(customerResult.customerId())
                .firstName(customerResult.firstName())
                .lastName(customerResult.lastName())
                .dni(customerResult.dni())
                .build();
    }

    private GetOrderResponse.AddressResponse mapFromAddressResultToAddressResponse(
            GetOrderResult.AddressResult addressResult) {
        return GetOrderResponse.AddressResponse.builder()
                .street(addressResult.street())
                .city(addressResult.city())
                .province(addressResult.province())
                .postalCode(addressResult.postalCode())
                .country(addressResult.country())
                .build();
    }

    private List<GetOrderResponse.OrderLineResponse> mapFromOrderLineResultsToOrderLineResponses(
            List<GetOrderResult.OrderLineResult> orderLineResults) {
        return orderLineResults.stream()
                .map(this::mapFromOrderLineResultToOrderLineResponse)
                .toList();
    }

    private GetOrderResponse.OrderLineResponse mapFromOrderLineResultToOrderLineResponse(
            GetOrderResult.OrderLineResult orderLineResult) {
        return GetOrderResponse.OrderLineResponse.builder()
                .orderLineId(orderLineResult.orderLineId())
                .orderId(orderLineResult.orderId())
                .productId(orderLineResult.productId())
                .productName(orderLineResult.productName())
                .productProviderReference(orderLineResult.productProviderReference())
                .categoryName(orderLineResult.categoryName())
                .providerName(orderLineResult.providerName())
                .quantity(orderLineResult.quantity())
                .unitPrice(orderLineResult.unitPrice())
                .subTotal(orderLineResult.subTotal())
                .build();
    }
}
