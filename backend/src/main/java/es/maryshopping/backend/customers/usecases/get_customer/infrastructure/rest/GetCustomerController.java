package es.maryshopping.backend.customers.usecases.get_customer.infrastructure.rest;

import es.maryshopping.backend.customers.usecases.get_customer.application.GetCustomerService;
import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerQuery;
import es.maryshopping.backend.shared_kernel.internal_api.customer.application.GetCustomerResult;
import es.maryshopping.backend.shared_kernel.security.CustomerOwnershipValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class GetCustomerController {
    private final GetCustomerService getCustomerService;
    private final CustomerOwnershipValidator customerOwnershipValidator;

    public GetCustomerController(GetCustomerService getCustomerService,
                                 CustomerOwnershipValidator customerOwnershipValidator) {
        this.getCustomerService = getCustomerService;
        this.customerOwnershipValidator = customerOwnershipValidator;
    }
    @GetMapping("/{customerId}")
    public ResponseEntity<GetCustomerResponse> proceed(@PathVariable UUID customerId){
        customerOwnershipValidator.validate(customerId);
        GetCustomerQuery query = mapFromPathToQuery(customerId);
        GetCustomerResult result = getCustomerService.proceed(query);
        GetCustomerResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }
    private GetCustomerQuery mapFromPathToQuery(UUID customerId){
        return new GetCustomerQuery(customerId);
    }
    private GetCustomerResponse mapFromResultToResponse(GetCustomerResult result){
        return GetCustomerResponse.builder()
                .id(result.id())
                .firstName(result.firstName())
                .lastName(result.lastName())
                .dni(result.dni())
                .phoneNumber(result.phoneNumber())
                .emailAddress(result.emailAddress())
                .billingAddress(mapFromAddressFieldsToBillingAddressResponse(result))
                .shippingAddress(mapFromAddressFieldsToShippingAddressResponse(result))
                .build();
    }

    private GetCustomerResponse.AddressResponse mapFromAddressFieldsToBillingAddressResponse(GetCustomerResult result) {
        return GetCustomerResponse.AddressResponse.builder()
                .street(result.billingAddressStreet())
                .city(result.billingAddressCity())
                .province(result.billingAddressProvince())
                .postalCode(result.billingAddressPostalCode())
                .country(result.billingAddressCountry())
                .build();
    }

    private GetCustomerResponse.AddressResponse mapFromAddressFieldsToShippingAddressResponse(GetCustomerResult result) {
        return GetCustomerResponse.AddressResponse.builder()
                .street(result.shippingAddressStreet())
                .city(result.shippingAddressCity())
                .province(result.shippingAddressProvince())
                .postalCode(result.shippingAddressPostalCode())
                .country(result.shippingAddressCountry())
                .build();
    }
}
