package telran.java57.productservice.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import telran.java57.productservice.dto.CreateProductDto;
import telran.java57.productservice.dto.ResponseProductDto;
import telran.java57.productservice.dto.UpdateProductDto;
import telran.java57.productservice.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService marketService;

    @PostMapping
    public ResponseProductDto createProduct(
            @RequestBody CreateProductDto dto,
            HttpServletRequest request
    ) {

        System.out.println("LOGIN=" + request.getHeader("X-User-Login"));
        System.out.println("ROLES=" + request.getHeader("X-User-Roles"));

        String login = request.getHeader("X-User-Login");
        if (login == null || login.isBlank()) {
            throw new AccessDeniedException("Missing X-User-Login header");
        }

        return marketService.addNewProduct(dto, login);
    }

    @PutMapping("/{id}")
    public ResponseProductDto updateProduct(
            @PathVariable String id,
            @RequestBody UpdateProductDto dto,
            HttpServletRequest request
    ) {
        String login = request.getHeader("X-User-Login");
        if (login == null || login.isBlank()) {
            throw new AccessDeniedException("Missing X-User-Login header");
        }
        String roles = request.getHeader("X-User-Roles");
        if (roles == null) {
            roles = "";
        }
        return marketService.updateProduct(id, dto, login, roles);
    }

    @GetMapping("/{id}")
    public ResponseProductDto getProduct(@PathVariable String id) {
        return marketService.getProduct(id);
    }

    @GetMapping
    public List<ResponseProductDto> getAllProducts() {
        return marketService.getAllProducts();
    }

    @DeleteMapping("/{id}")
    public ResponseProductDto deleteProduct(
            @PathVariable String id,
            HttpServletRequest request
    ) {
        String login = request.getHeader("X-User-Login");
        if (login == null || login.isBlank()) {
            throw new AccessDeniedException("Missing X-User-Login header");
        }
        String roles = request.getHeader("X-User-Roles");
        if (roles == null) {
            roles = "";
        }
        return marketService.deleteProduct(id, login, roles);
    }

    @GetMapping("/category/{category}")
    public List<ResponseProductDto> getByCategory(@PathVariable String category) {
        return marketService.getByCategory(category);
    }

    @GetMapping("/my-products")
    public List<ResponseProductDto> getMyProducts(HttpServletRequest request) {
        String login = request.getHeader("X-User-Login");
        if (login == null || login.isBlank()) {
            throw new AccessDeniedException("Missing X-User-Login header");
        }
        return marketService.getProductsBySupplier(login);
    }

    @PutMapping("/{id}/status")
    public ResponseProductDto toggleProductStatus(
            @PathVariable String id,
            @RequestParam boolean block
    ) {
        return marketService.toggleProductStatus(id, block);
    }

    @GetMapping("/blocked")
    public List<ResponseProductDto> getBlocked() {
        return marketService.getBlockedProducts();
    }
}