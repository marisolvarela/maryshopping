package es.maryshopping.backend.customers.shared.infrastructure.persistence;

import es.maryshopping.backend.customers.shared.domain.customer.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="customer", schema = "customers")
public class CustomerEntity {
    @Id
    UUID id;
    String firstName;
    String lastName;
    String dni;
    String phoneNumber;
    String emailAddress;
    String billingAddressStreet;
    String billingAddressCity;
    String billingAddressProvince;
    String billingAddressPostalCode;
    String billingAddressCountry;
    String shippingAddressStreet;
    String shippingAddressCity;
    String shippingAddressProvince;
    String shippingAddressPostalCode;
    String shippingAddressCountry;

    public static CustomerEntity fromDomain(Customer customer) {
        return new CustomerEntity(
                customer.id().value(),
                customer.firstName().value(),
                customer.lastName().value(),
                customer.dni().value(),
                customer.phoneNumber().value(),
                customer.emailAddress().value(),
                customer.billingAddress().value().street().value(),
                customer.billingAddress().value().city().value(),
                customer.billingAddress().value().province().value(),
                customer.billingAddress().value().postalCode().value(),
                customer.billingAddress().value().country().value(),
                customer.shippingAddress().value().street().value(),
                customer.shippingAddress().value().city().value(),
                customer.shippingAddress().value().province().value(),
                customer.shippingAddress().value().postalCode().value(),
                customer.shippingAddress().value().country().value()
        );
    }
}
