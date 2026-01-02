package telran.java57.farmmarket.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import telran.java57.farmmarket.dao.OrderRepository;
import telran.java57.farmmarket.dto.OrderDto;
import telran.java57.farmmarket.dto.OrderResponseDto;
import telran.java57.farmmarket.dto.exceptions.NotEnoughQuantityOfProductException;
import telran.java57.farmmarket.dto.exceptions.OrderNotFoundException;
import telran.java57.farmmarket.dto.exceptions.ProductNotFoundException;
import telran.java57.farmmarket.model.*;
import telran.java57.farmmarket.dao.ProductCacheRepository;
import telran.java57.farmmarket.service.kafka.Producer.OrderKafkaProducer;
import telran.java57.sharedkafkaevents.kafka.events.OrderItemEvent;
import telran.java57.sharedkafkaevents.kafka.events.OrderPaidEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final ProductCacheRepository productCacheRepository;
    private final OrderKafkaProducer orderKafkaProducer;


    @Override
    public OrderResponseDto createOrder(OrderDto dto, String userLogin) {

        Map<String, Integer> productQuantities = dto.getProductQuantities();

        if (productQuantities == null || productQuantities.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product");
        }

        List<OrderItem> items = new ArrayList<>();
        double total = 0;

        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {

            String productId = entry.getKey();
            int requestedQty = entry.getValue();

            ProductCache cache = productCacheRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(productId));

            if (!"ACTIVE".equals(cache.getStatus())) {
                throw new IllegalStateException("Product not available: " + cache.getName());
            }

            if (cache.getQuantity() < requestedQty) {
                throw new NotEnoughQuantityOfProductException(cache.getName());
            }

            items.add(OrderItem.builder()
                    .productId(productId)
                    .name(cache.getName())
                    .supplierLogin(cache.getSupplierLogin())
                    .quantity(requestedQty)
                    .priceAtPurchase(cache.getPrice())
                    .build());

            total += cache.getPrice() * requestedQty;
        }

        Order order = Order.builder()
                .userLogin(userLogin)
                .items(items)
                .status(OrderStatus.CREATED)
                .totalPrice(total)
                .createdAt(Instant.now())
                .build();

        orderRepository.save(order);

        return modelMapper.map(order, OrderResponseDto.class);
    }

    @Override
    public void deleteCreatedOrder(String orderId, String userLogin) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getUserLogin().equals(userLogin)) {
            throw new AccessDeniedException("You can delete only your own order");
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be deleted");
        }

        orderRepository.delete(order);
    }
    @Override
    public OrderResponseDto markOrderAsPaid(String orderId, String userLogin) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));


        if (!order.getUserLogin().equals(userLogin)) {
            throw new AccessDeniedException("You can only pay your own order");
        }


        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Order is not payable");
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(Instant.now());
        orderRepository.save(order);


        orderKafkaProducer.sendOrderPaid(
                OrderPaidEvent.builder()
                        .orderId(order.getId())
                        .paidAt(order.getPaidAt())
                        .version(1)
                        .items(
                                order.getItems().stream()
                                        .map(i -> OrderItemEvent.builder()
                                                .productId(i.getProductId())
                                                .quantity(i.getQuantity())
                                                .build()
                                        )
                                        .toList()
                        )
                        .build()
        );

        return modelMapper.map(order, OrderResponseDto.class);
    }




    @Override
    public List<OrderResponseDto> getMyOrders(String userLogin) {
        return orderRepository.findAllByUserLoginOrderByCreatedAtDesc(userLogin)
                .stream()
                .map(o -> modelMapper.map(o, OrderResponseDto.class))
                .toList();
    }

    @Override
    public List<OrderResponseDto> getOrdersBySupplierLogin(String supplierLogin) {
        return orderRepository.findByItemsSupplierLogin(supplierLogin, Pageable.unpaged())
                .map(o -> modelMapper.map(o, OrderResponseDto.class))
                .getContent();
    }

    @Override
    public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(o -> modelMapper.map(o, OrderResponseDto.class));
    }
}


