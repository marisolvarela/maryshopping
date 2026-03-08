package es.maryshopping.backend.orders.shared.infrastructure.persistence;

import es.maryshopping.backend.orders.shared.domain.order.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "purchase_order",schema="orders")
public class OrderEntity {
    @Id
    UUID id;
    UUID customerId;
    Long orderDate;
    String status;
    String shippingStreet;
    String shippingCity;
    String shippingProvince;
    String shippingPostalCode;
    String shippingCountry;

  public static OrderEntity fromDomain(Order order){
      return OrderEntity.builder()
              .id(order.id().value())
              .customerId(order.customerId().value())
              .orderDate(order.orderDate().value())
              .status(order.status().value().name())
              .shippingStreet(order.shippingAddress().street().value())
              .shippingCity(order.shippingAddress().city().value())
              .shippingProvince(order.shippingAddress().province().value())
              .shippingPostalCode(order.shippingAddress().postalCode().value())
              .shippingCountry(order.shippingAddress().country().value())
              .build();
  }
}
