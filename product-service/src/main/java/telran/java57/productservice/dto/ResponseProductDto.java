package telran.java57.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseProductDto {
    private String id;
    private String name;
    private Double price;
    private int quantity;
    private String category;
    private String imageUrl;
    private String fileId;
}
