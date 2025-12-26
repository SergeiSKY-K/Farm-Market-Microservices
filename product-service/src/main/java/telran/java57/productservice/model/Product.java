package telran.java57.productservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Document(collection = "products")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Product {
    @Id
    private String id;
    @Setter
    private String name;
    @Setter
    private Double price;
    @Setter
    private int quantity;
    @Setter
    private String category;
    @Setter
    private String imageUrl;
    @Setter
    private String fileId;
    @Setter
    private String supplierLogin;
    @Setter
    private ProductStatus status;
    @CreatedDate
    @Setter
    private Instant createdAt;
}
