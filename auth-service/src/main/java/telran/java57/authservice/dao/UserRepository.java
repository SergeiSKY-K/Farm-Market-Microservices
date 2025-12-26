package telran.java57.authservice.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import telran.java57.authservice.model.UserAccount;



public interface UserRepository extends MongoRepository<UserAccount,String> {
}
