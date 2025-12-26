package telran.java57.sharedkafkaevents.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {

    private String productId;
    private String name;
    private int quantity;
    private double price;
    private String supplierLogin;
    private String status;

    private int version = 1;
}