package telran.java57.productservice.service;

import telran.java57.productservice.dto.CreateProductDto;
import telran.java57.productservice.dto.ResponseProductDto;
import telran.java57.productservice.dto.UpdateProductDto;

import java.util.List;

public interface ProductService {

    ResponseProductDto addNewProduct(CreateProductDto dto, String login);

    ResponseProductDto updateProduct(String id, UpdateProductDto dto, String login, String roles);

    ResponseProductDto deleteProduct(String id, String login, String roles);

    ResponseProductDto getProduct(String id);

    List<ResponseProductDto> getAllProducts();

    List<ResponseProductDto> getByCategory(String category);

    List<ResponseProductDto> getProductsBySupplier(String supplierLogin);

    ResponseProductDto toggleProductStatus(String id, boolean block);

    List<ResponseProductDto> getBlockedProducts();
}
