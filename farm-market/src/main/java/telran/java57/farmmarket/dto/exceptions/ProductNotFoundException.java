package telran.java57.farmmarket.dto.exceptions;

public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(String id) {
    super("Product with id " + id + " not found");
  }
}