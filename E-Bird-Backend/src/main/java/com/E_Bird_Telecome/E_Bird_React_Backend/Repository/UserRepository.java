//package com.E_Bird_Telecome.E_Bird_React_Backend.Repository;
//
//
//import com.E_Bird_Telecome.E_Bird_React_Backend.Model.User;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface UserRepository extends MongoRepository<User, String> {
//    List<User> findByIsActiveTrue();
//    User findByEmail(String email);
//    User findByAzureObjectId(String azureObjectId);
//}


package com.E_Bird_Telecome.E_Bird_React_Backend.Repository;

import com.E_Bird_Telecome.E_Bird_React_Backend.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByIsActiveTrue();
    User findByEmail(String email);
    User findByAzureObjectId(String azureObjectId);
}