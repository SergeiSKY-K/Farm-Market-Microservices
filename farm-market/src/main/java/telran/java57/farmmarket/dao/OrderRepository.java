package telran.java57.farmmarket.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import telran.java57.farmmarket.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findAllByUserLoginOrderByCreatedAtDesc(String userLogin);

    Optional<Order> findByIdAndUserLogin(String id, String userLogin);

    @Query("{ 'items.supplierLogin': ?0 }")
    Page<Order> findByItemsSupplierLogin(String supplierLogin, Pageable pageable);

    Page<Order> findAll(Pageable pageable);
}