package telran.java57.farmmarket.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import telran.java57.farmmarket.model.ProductCache;

public interface ProductCacheRepository extends MongoRepository<ProductCache, String> { }

