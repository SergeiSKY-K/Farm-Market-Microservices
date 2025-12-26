package telran.java57.productservice.service;

import io.imagekit.sdk.ImageKit;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telran.java57.productservice.dao.ProductRepository;
import telran.java57.productservice.dto.CreateProductDto;
import telran.java57.productservice.dto.ResponseProductDto;
import telran.java57.productservice.dto.UpdateProductDto;
import telran.java57.productservice.dto.exceptions.ProductNotFoundException;
import telran.java57.sharedkafkaevents.kafka.events.ProductCreatedEvent;
import telran.java57.sharedkafkaevents.kafka.events.ProductDeletedEvent;
import telran.java57.sharedkafkaevents.kafka.events.ProductUpdatedEvent;
import telran.java57.productservice.model.Product;
import telran.java57.productservice.model.ProductStatus;
import telran.java57.productservice.kafka.producer.KafkaProducerService;

import java.util.List;



@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public ResponseProductDto addNewProduct(CreateProductDto dto, String login) {

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (dto.getPrice() == null || dto.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        if (dto.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }

        Product product = modelMapper.map(dto, Product.class);

        product.setSupplierLogin(login);
        product.setStatus(ProductStatus.ACTIVE);

        product = productRepository.save(product);

        kafkaProducerService.sendCreated(
                new ProductCreatedEvent(
                        product.getId(),
                        product.getName(),
                        product.getQuantity(),
                        product.getPrice(),
                        product.getSupplierLogin(),
                        product.getStatus().name(),
                        1
                )
        );
        ;

        return modelMapper.map(product, ResponseProductDto.class);
    }

    @Transactional
    @Override
    public ResponseProductDto updateProduct(String id, UpdateProductDto dto, String login, String roles) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        boolean isAdmin = roles != null && roles.contains("ADMINISTRATOR");
        boolean isOwner = login.equals(product.getSupplierLogin());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You can update only your own products.");
        }

        if (dto.getFileId() != null) {
            String newFileId = dto.getFileId();
            String oldFileId = product.getFileId();

            if (newFileId != null && !newFileId.isBlank()
                    && oldFileId != null && !oldFileId.isBlank()
                    && !newFileId.equals(oldFileId)) {
                try {
                    ImageKit.getInstance().deleteFile(oldFileId);
                } catch (Exception e) {
                    System.err.println(" Failed to delete old image in ImageKit: " + e.getMessage());
                }
            }

            product.setFileId(newFileId);
        }

        if (dto.getName() != null) {
            String name = dto.getName().trim();
            if (name.isEmpty()) throw new IllegalArgumentException("Product name is required");
            product.setName(name);
        }

        if (dto.getPrice() != null) {
            if (dto.getPrice() <= 0)
                throw new IllegalArgumentException("Product price must be positive");
            product.setPrice(dto.getPrice());
        }

        if (dto.getQuantity() != null) {
            if (dto.getQuantity() < 0)
                throw new IllegalArgumentException("Quantity must be non-negative");
            product.setQuantity(dto.getQuantity());
        }

        if (dto.getCategory() != null) {
            product.setCategory(dto.getCategory());
        }

        if (dto.getImageUrl() != null) {
            product.setImageUrl(dto.getImageUrl());
        }

        product = productRepository.save(product);

        kafkaProducerService.sendUpdated(
                new ProductUpdatedEvent(
                        product.getId(),
                        product.getName(),
                        product.getQuantity(),
                        product.getPrice(),
                        product.getSupplierLogin(),
                        product.getStatus().name(),
                        1
                ));

        return modelMapper.map(product, ResponseProductDto.class);
    }

    @Override
    public ResponseProductDto getProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return modelMapper.map(product, ResponseProductDto.class);
    }

    @Override
    public List<ResponseProductDto> getAllProducts() {
        return productRepository.findByStatusOrderByCreatedAtDesc(ProductStatus.ACTIVE)
                .stream()
                .map(p -> modelMapper.map(p, ResponseProductDto.class))
                .toList();
    }

    @Override
    public ResponseProductDto deleteProduct(String id, String login, String roles) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        boolean isAdmin = roles != null && roles.contains("ADMINISTRATOR");
        boolean isOwner = login.equals(product.getSupplierLogin());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You can delete only your own products.");
        }

        if (product.getFileId() != null && !product.getFileId().isBlank()) {
            try {
                ImageKit.getInstance().deleteFile(product.getFileId());
            } catch (Exception e) {
                System.err.println("Failed to delete image: " + e.getMessage());
            }
        }

        kafkaProducerService.sendDeleted(new ProductDeletedEvent(product.getId(), 1));

        productRepository.deleteById(id);

        return modelMapper.map(product, ResponseProductDto.class);
    }

    @Override
    public List<ResponseProductDto> getByCategory(String category) {
        return productRepository
                .findByCategoryAndStatusOrderByCreatedAtDesc(category, ProductStatus.ACTIVE)
                .stream()
                .map(p -> modelMapper.map(p, ResponseProductDto.class))
                .toList();
    }

    @Override
    public List<ResponseProductDto> getProductsBySupplier(String supplierLogin) {
        return productRepository.findBySupplierLoginOrderByCreatedAtDesc(supplierLogin)
                .stream()
                .map(p -> modelMapper.map(p, ResponseProductDto.class))
                .toList();
    }

    @Override
    public ResponseProductDto toggleProductStatus(String id, boolean block) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setStatus(block ? ProductStatus.BLOCKED : ProductStatus.ACTIVE);
        product = productRepository.save(product);


        kafkaProducerService.sendUpdated(
                new ProductUpdatedEvent(
                        product.getId(),
                        product.getName(),
                        product.getQuantity(),
                        product.getPrice(),
                        product.getSupplierLogin(),
                        product.getStatus().name(),
                        1
                )
        );

        return modelMapper.map(product, ResponseProductDto.class);
    }


    @Override
    public List<ResponseProductDto> getBlockedProducts() {
        return productRepository.findByStatusOrderByCreatedAtDesc(ProductStatus.BLOCKED)
                .stream()
                .map(p -> modelMapper.map(p, ResponseProductDto.class))
                .toList();
    }
}
