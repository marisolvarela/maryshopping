package es.maryshopping.backend.orders.usecases.get_all_orders.infrastructure.rest;

import es.maryshopping.backend.orders.usecases.get_all_orders.application.GetAllOrdersQuery;
import es.maryshopping.backend.orders.usecases.get_all_orders.application.GetAllOrdersResult;
import es.maryshopping.backend.orders.usecases.get_all_orders.application.GetAllOrdersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class GetAllOrdersController {

    private final GetAllOrdersService getAllOrdersService;

    public GetAllOrdersController(GetAllOrdersService getAllOrdersService) {
        this.getAllOrdersService = getAllOrdersService;
    }

    @GetMapping
    public ResponseEntity<GetAllOrdersResponse> proceed() {
        GetAllOrdersQuery query = createQuery();
        GetAllOrdersResult result = getAllOrdersService.proceed(query);
        GetAllOrdersResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private GetAllOrdersQuery createQuery() {
        return new GetAllOrdersQuery();
    }

    private GetAllOrdersResponse mapFromResultToResponse(GetAllOrdersResult result) {
        List<GetAllOrdersResponse.OrderResponse> orders = result.orders().stream()
                .map(this::mapFromOrderResultToOrderResponse)
                .toList();
        return GetAllOrdersResponse.builder()
                .orders(orders)
                .build();
    }

    private GetAllOrdersResponse.OrderResponse mapFromOrderResultToOrderResponse(
            GetAllOrdersResult.OrderResult orderResult) {
        return GetAllOrdersResponse.OrderResponse.builder()
                .orderId(orderResult.orderId())
                .orderDate(orderResult.orderDate())
                .customerId(orderResult.customerId())
                .firstName(orderResult.firstName())
                .lastName(orderResult.lastName())
                .dni(orderResult.dni())
                .shippingAddress(mapFromAddressResultToAddressResponse(orderResult.shippingAddress()))
                .orderLines(mapFromOrderLineResultsToOrderLineResponses(orderResult.orderLines()))
                .totalAmountDue(orderResult.totalAmountDue())
                .status(orderResult.status())
                .build();
    }

    private GetAllOrdersResponse.OrderResponse.AddressResponse mapFromAddressResultToAddressResponse(
            GetAllOrdersResult.OrderResult.AddressResult addressResult) {
        return GetAllOrdersResponse.OrderResponse.AddressResponse.builder()
                .street(addressResult.street())
                .city(addressResult.city())
                .province(addressResult.province())
                .postalCode(addressResult.postalCode())
                .country(addressResult.country())
                .build();
    }

    private List<GetAllOrdersResponse.OrderResponse.OrderLineResponse> mapFromOrderLineResultsToOrderLineResponses(
            List<GetAllOrdersResult.OrderResult.OrderLineResult> orderLineResults) {
        return orderLineResults.stream()
                .map(this::mapFromOrderLineResultToOrderLineResponse)
                .toList();
    }

    private GetAllOrdersResponse.OrderResponse.OrderLineResponse mapFromOrderLineResultToOrderLineResponse(
            GetAllOrdersResult.OrderResult.OrderLineResult orderLineResult) {
        return GetAllOrdersResponse.OrderResponse.OrderLineResponse.builder()
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

