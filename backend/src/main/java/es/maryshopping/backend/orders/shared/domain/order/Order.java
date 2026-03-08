package es.maryshopping.backend.orders.shared.domain.order;

import es.maryshopping.backend.orders.shared.domain.order_line.OrderLine;
import es.maryshopping.backend.orders.shared.domain.order_line.SubTotal;
import es.maryshopping.backend.shared_kernel.domain.address.Address;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record Order(
        OrderId id,
        OrderDate orderDate,
        CustomerId customerId,
        Address shippingAddress,
        Stock stock,
        List<OrderLine> lines,
        Status status
) {
    public TotalAmountDue total() {

        BigDecimal sum = lines.stream()
                .map(OrderLine::subTotal)
                .map(SubTotal::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TotalAmountDue(sum);
    }
}
