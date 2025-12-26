package telran.java57.farmmarket.dto;


import lombok.*;
import telran.java57.farmmarket.model.OrderStatus;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private String id;
    private String userLogin;
    private List<OrderItemDto> items;
    private OrderStatus status;
    private Double totalPrice;
    private Instant createdAt;
    private Instant paidAt;
}