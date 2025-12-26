package telran.java57.farmmarket.dto.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String id) {
        super("Order with id " + id + " not found");
    }
}