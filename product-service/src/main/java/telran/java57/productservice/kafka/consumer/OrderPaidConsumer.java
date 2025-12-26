package telran.java57.productservice.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import telran.java57.productservice.dao.ProductRepository;
import telran.java57.productservice.model.ProductStatus;
import telran.java57.sharedkafkaevents.kafka.events.OrderPaidEvent;
import telran.java57.sharedkafkaevents.kafka.events.OrderItemEvent;
import telran.java57.productservice.model.Product;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaidConsumer {

    private final ProductRepository productRepository;

    @KafkaListener(topics = "order-paid", groupId = "product-service")
    public void onOrderPaid(OrderPaidEvent event) {

        log.info("order payed event received : {}", event);

        for (OrderItemEvent item : event.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Product not found: " + item.getProductId()
                    ));

            int newQuantity = product.getQuantity() - item.getQuantity();
            product.setQuantity(newQuantity);

            if (newQuantity <= 0) {
                product.setStatus(ProductStatus.BLOCKED);
            }

            productRepository.save(product);

            log.info(
                    "product updated id={}, quantity={}, status={}",
                    product.getId(),
                    product.getQuantity(),
                    product.getStatus()
            );
        }
    }
}
