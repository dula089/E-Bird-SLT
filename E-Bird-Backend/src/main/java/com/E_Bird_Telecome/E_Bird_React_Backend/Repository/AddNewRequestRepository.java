package com.E_Bird_Telecome.E_Bird_React_Backend.Repository;

import com.E_Bird_Telecome.E_Bird_React_Backend.Model.AddNewRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddNewRequestRepository extends MongoRepository<AddNewRequest, String> {
    long countByRequestIdStartingWith(String prefix);
}

