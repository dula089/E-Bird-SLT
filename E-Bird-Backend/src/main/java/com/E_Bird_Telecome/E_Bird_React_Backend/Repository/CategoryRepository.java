package com.E_Bird_Telecome.E_Bird_React_Backend.Repository;

import com.E_Bird_Telecome.E_Bird_React_Backend.Model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findByName(String name);

    // Find all by type ("category" or "organization")
    List<Category> findByType(String type);
}
