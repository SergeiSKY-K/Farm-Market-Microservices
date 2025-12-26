package telran.java57.authservice.dto.exceptions;

public class NotEnoughQuantityOfProductException extends RuntimeException {
    public NotEnoughQuantityOfProductException(String productName) {
        super("Not enough quantity of product: " + productName);
    }
}
