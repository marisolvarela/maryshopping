package es.maryshopping.backend.reports.shared.infrastructure.persistence;

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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_report", schema = "reports")
public class OrderReportEntity {
    // Order
    private UUID orderId;
    private Long orderDate;

    // Customer
    private UUID customerId;
    private String customerFirstName;
    private String customerLastName;
    private String customerDni;

    // Shipping address
    private String shippingStreet;
    private String shippingCity;
    private String shippingProvince;
    private String shippingPostalCode;
    private String shippingCountry;

    // Order line
    @Id
    private UUID orderLineId;

    private UUID productId;
    private String productName;
    private String productProviderReference;
    private String categoryName;
    private String providerName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;

    // Totals
    private BigDecimal totalAmountDue;
}

