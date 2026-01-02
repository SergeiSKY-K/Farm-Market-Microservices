package telran.java57.farmmarket.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import telran.java57.farmmarket.dto.OrderDto;
import telran.java57.farmmarket.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(OrderDto orderDto, String userLogin);
    OrderResponseDto markOrderAsPaid(String orderId, String userLogin);
    List<OrderResponseDto> getMyOrders(String userLogin);
    List<OrderResponseDto> getOrdersBySupplierLogin(String supplierLogin);
    Page<OrderResponseDto> getAllOrders(Pageable pageable);

    void deleteCreatedOrder(String orderId, String userLogin);
}
