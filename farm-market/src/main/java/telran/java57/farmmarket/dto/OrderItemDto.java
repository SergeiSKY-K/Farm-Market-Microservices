package telran.java57.farmmarket.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private String productId;
    private String name;
    private String supplierLogin;
    private Integer quantity;
    private Double priceAtPurchase;
}
