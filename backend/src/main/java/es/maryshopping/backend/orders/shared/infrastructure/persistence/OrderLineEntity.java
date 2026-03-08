package es.maryshopping.backend.orders.shared.infrastructure.persistence;


import es.maryshopping.backend.orders.shared.domain.order_line.OrderLine;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="order_line", schema = "orders")
public class OrderLineEntity {
    @Id
    UUID orderLineId;
    UUID orderId;
    UUID productId;
    String productName;
    String productProviderReference;
    String categoryName;
    String providerName;
    Integer quantity;
    BigDecimal unitPrice;

    public static OrderLineEntity fromDomain(OrderLine orderLine){
        return new OrderLineEntity(
                orderLine.id().value(),
                orderLine.orderId().value(),
                orderLine.productId().value(),
                orderLine.productName().value(),
                orderLine.productProviderReference().value(),
                orderLine.categoryName().value(),
                orderLine.providerName().value(),
                orderLine.quantity().value(),
                orderLine.unitPrice().value()
        );
    }
}
