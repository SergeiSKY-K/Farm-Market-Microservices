package telran.java57.sharedkafkaevents.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeletedEvent {

    private String productId;
    private int version = 1;
}