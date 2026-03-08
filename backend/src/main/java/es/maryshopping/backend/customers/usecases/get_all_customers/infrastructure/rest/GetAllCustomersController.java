package es.maryshopping.backend.customers.usecases.get_all_customers.infrastructure.rest;


import es.maryshopping.backend.customers.usecases.get_all_customers.application.CustomerResult;
import es.maryshopping.backend.customers.usecases.get_all_customers.application.GetAllCustomersQuery;
import es.maryshopping.backend.customers.usecases.get_all_customers.application.GetAllCustomersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class GetAllCustomersController {
    private final GetAllCustomersService GetAllCustomersService;

    public GetAllCustomersController(GetAllCustomersService GetAllCustomersService) {
        this.GetAllCustomersService = GetAllCustomersService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> proceed() {
        GetAllCustomersQuery query = createQuery();
        List<CustomerResult> customerResults = GetAllCustomersService.proceed(query);
        List<CustomerResponse> customersResponses = customerResults.stream()
                .map(this::mapFromResultToResponse)
                .toList();
        return ResponseEntity.status(200).body(customersResponses);
    }

    private GetAllCustomersQuery createQuery(){
        return new GetAllCustomersQuery();
    }

    private CustomerResponse mapFromResultToResponse(CustomerResult result) {
        return CustomerResponse.builder()
                .id(result.id())
                .firstName(result.firstName())
                .lastName(result.lastName())
                .dni(result.dni())
                .phoneNumber(result.phoneNumber())
                .emailAddress(result.emailAddress())
                .billingAddress(mapFromAddressFieldsToBillingAddressResponse(result))
                .ShippingAddress(mapFromAddressFieldsToShippingAddressResponse(result))
                .build();
    }

    private CustomerResponse.AddressResponse mapFromAddressFieldsToBillingAddressResponse(CustomerResult result) {
        return CustomerResponse.AddressResponse.builder()
                .street(result.billingAddressStreet())
                .city(result.billingAddressCity())
                .province(result.billingAddressProvince())
                .postalCode(result.billingAddressPostalCode())
                .country(result.billingAddressCountry())
                .build();
    }

    private CustomerResponse.AddressResponse mapFromAddressFieldsToShippingAddressResponse(CustomerResult result) {
        return CustomerResponse.AddressResponse.builder()
                .street(result.shippingAddressStreet())
                .city(result.shippingAddressCity())
                .province(result.shippingAddressProvince())
                .postalCode(result.shippingAddressPostalCode())
                .country(result.shippingAddressCountry())
                .build();
    }
}
