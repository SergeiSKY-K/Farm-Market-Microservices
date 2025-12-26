package telran.java57.farmmarket.service.kafka.Producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import telran.java57.sharedkafkaevents.kafka.events.OrderPaidEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ORDER_PAID_TOPIC = "order-paid";

    public void sendOrderPaid(OrderPaidEvent event) {
        try {
            kafkaTemplate.send(ORDER_PAID_TOPIC, event);
            log.info("Order PAID event sent: {}", event);
        } catch (Exception e) {
            log.error("Failed to send OrderPaidEvent {}", event, e);
        }
    }
}