package telran.java57.sharedkafkaevents.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPaidEvent {

    private String orderId;

    private List<OrderItemEvent> items;

    private Instant paidAt;

    private int version;
}