package telran.java57.farmmarket.service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import telran.java57.farmmarket.dao.ProductCacheRepository;
import telran.java57.farmmarket.model.ProductCache;
import telran.java57.farmmarket.model.ProductStatus;
import telran.java57.sharedkafkaevents.kafka.events.OrderPaidEvent;
import telran.java57.sharedkafkaevents.kafka.events.OrderItemEvent;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaidCacheConsumer {

    private final ProductCacheRepository productCacheRepository;

    @KafkaListener(topics = "order-paid", groupId = "farm-market")
    public void onOrderPaid(OrderPaidEvent event) {

        log.info("cache update event: {}", event);

        for (OrderItemEvent item : event.getItems()) {

            ProductCache cache = productCacheRepository.findById(item.getProductId())
                    .orElse(null);

            if (cache == null) {
                log.warn(" Cache not found for product {}", item.getProductId());
                continue;
            }

            int newQuantity = cache.getQuantity() - item.getQuantity();
            cache.setQuantity(newQuantity);

            if (newQuantity <= 0) {
                cache.setStatus(String.valueOf(ProductStatus.BLOCKED));
            }

            cache.setUpdatedAt(Instant.now());

            productCacheRepository.save(cache);

            log.info(
                    "cache updated product ={}, quantity={}",
                    cache.getProductId(),
                    cache.getQuantity()
            );
        }
    }
}
