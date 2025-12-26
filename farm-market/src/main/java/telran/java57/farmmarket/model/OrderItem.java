package telran.java57.farmmarket.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    private String productId;
    private String name;
    private String supplierLogin;
    private Integer quantity;
    private Double priceAtPurchase;
}
