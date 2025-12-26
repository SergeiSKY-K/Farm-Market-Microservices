package telran.java57.farmmarket.dto;


import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Map<String,Integer> productQuantities;
}