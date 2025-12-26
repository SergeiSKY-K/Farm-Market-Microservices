package telran.java57.farmmarket.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    private String id;

    private String userLogin;

    private List<OrderItem> items;

    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;

    private Double totalPrice;

//    private PaymentStatus paymentStatus;

    private Instant createdAt;

    private Instant paidAt;

}
