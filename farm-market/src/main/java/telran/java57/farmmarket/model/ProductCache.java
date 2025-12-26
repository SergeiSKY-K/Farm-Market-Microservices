package telran.java57.farmmarket.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "product_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCache {

    @Id
    private String productId;

    private String name;
    private int quantity;
    private double price;
    private String supplierLogin;
    private String status;

    private Instant updatedAt;
}