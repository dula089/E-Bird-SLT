package com.E_Bird_Telecome.E_Bird_React_Backend.Service;

import com.E_Bird_Telecome.E_Bird_React_Backend.Model.Category;
import com.E_Bird_Telecome.E_Bird_React_Backend.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public Category addCategory(Category category) {
        // You might want to add validation to check for duplicate name here
        return repository.save(category);
    }

    public List<Category> getAllCategories() {
        return repository.findByType("category");
    }

    public List<Category> getAllOrganizations() {
        return repository.findByType("organization");
    }

    public Optional<Category> getCategoryById(String id) {
        return repository.findById(id);
    }

    public Category updateCategory(String id, Category updatedCategory) {
        updatedCategory.setId(id);
        return repository.save(updatedCategory);
    }

    public void deleteCategory(String id) {
        repository.deleteById(id);
    }
}
