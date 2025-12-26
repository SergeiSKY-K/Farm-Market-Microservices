package telran.java57.authservice.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import telran.java57.authservice.entity.RefreshTokenEntity;

public interface RefreshTokenRepository extends MongoRepository<RefreshTokenEntity,String> {

}
