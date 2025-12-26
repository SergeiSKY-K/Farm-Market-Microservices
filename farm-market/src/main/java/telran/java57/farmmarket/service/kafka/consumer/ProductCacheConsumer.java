package telran.java57.farmmarket.service.kafka.consumer;

import telran.java57.farmmarket.dao.ProductCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import telran.java57.sharedkafkaevents.kafka.events.ProductCreatedEvent;
import telran.java57.sharedkafkaevents.kafka.events.ProductDeletedEvent;
import telran.java57.sharedkafkaevents.kafka.events.ProductUpdatedEvent;
import telran.java57.farmmarket.model.ProductCache;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCacheConsumer {

    private final ProductCacheRepository repository;

    @KafkaListener(topics = "product-created", groupId = "order-service")
    public void onCreated(ProductCreatedEvent ev) {
        log.info("🔥 Cache CREATED {}", ev);

        ProductCache cache = ProductCache.builder()
                .productId(ev.getProductId())
                .name(ev.getName())
                .quantity(ev.getQuantity())
                .price(ev.getPrice())
                .supplierLogin(ev.getSupplierLogin())
                .status(ev.getStatus())
                .updatedAt(Instant.now())
                .build();

        repository.save(cache);
    }

    @KafkaListener(topics = "product-updated", groupId = "order-service")
    public void onUpdated(ProductUpdatedEvent ev) {
        log.info("🔥 Cache UPDATED {}", ev);

        ProductCache cache = repository.findById(ev.getProductId())
                .orElseThrow(() ->
                        new IllegalStateException("Cache missing for product " + ev.getProductId())
                );

        cache.setName(ev.getName());
        cache.setQuantity(ev.getQuantity());
        cache.setPrice(ev.getPrice());
        cache.setSupplierLogin(ev.getSupplierLogin());
        cache.setStatus(ev.getStatus());
        cache.setUpdatedAt(Instant.now());

        repository.save(cache);
    }

    @KafkaListener(topics = "product-deleted", groupId = "order-service")
    public void onDeleted(ProductDeletedEvent ev) {
        log.info("🔥 Cache DELETED {}", ev);
        repository.deleteById(ev.getProductId());
    }


}
