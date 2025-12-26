package telran.java57.productservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import telran.java57.sharedkafkaevents.kafka.events.ProductCreatedEvent;
import telran.java57.sharedkafkaevents.kafka.events.ProductDeletedEvent;
import telran.java57.sharedkafkaevents.kafka.events.ProductUpdatedEvent;
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    private static final String PRODUCT_CREATED_TOPIC = "product-created";
    private static final String PRODUCT_UPDATED_TOPIC = "product-updated";
    private static final String PRODUCT_DELETED_TOPIC = "product-deleted";

    public void sendCreated(ProductCreatedEvent event) {
        kafkaTemplate.send(PRODUCT_CREATED_TOPIC, event);
        System.out.println("📤 Created event to Kafka: " + event);
    }

    public void sendUpdated(ProductUpdatedEvent event) {
        kafkaTemplate.send(PRODUCT_UPDATED_TOPIC, event);
        System.out.println("📤 Updated event to Kafka: " + event);
    }

    public void sendDeleted(ProductDeletedEvent event) {
        kafkaTemplate.send(PRODUCT_DELETED_TOPIC, event);
        System.out.println("📤 Deleted event to Kafka: " + event);
    }
}
