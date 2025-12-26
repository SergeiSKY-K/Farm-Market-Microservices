package telran.java57.productservice.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductDto {
    private String name;
    private Double price;
    private Integer quantity;
    private String category;
    private String imageUrl;
    private String fileId;
}
