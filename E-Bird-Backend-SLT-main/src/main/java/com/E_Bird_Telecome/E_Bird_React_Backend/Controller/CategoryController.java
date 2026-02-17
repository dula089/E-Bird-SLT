package com.E_Bird_Telecome.E_Bird_React_Backend.Controller;

import com.E_Bird_Telecome.E_Bird_React_Backend.Model.Category;
import com.E_Bird_Telecome.E_Bird_React_Backend.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService service;

    // POST to add either Category or Organization
    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        // Ensure category.type is either "category" or "organization"
        if (!"category".equals(category.getType()) && !"organization".equals(category.getType())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.addCategory(category));
    }

    // GET all Categories (type=category)
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(service.getAllCategories());
    }

    // GET all Organizations (type=organization)
    @GetMapping("/Organizations")
    public ResponseEntity<List<Category>> getAllOrganizations() {
        return ResponseEntity.ok(service.getAllOrganizations());
    }

    // GET by ID (category or organization)
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        Optional<Category> cat = service.getCategoryById(id);
        return cat.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // UPDATE by ID
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable String id, @RequestBody Category updatedCategory) {
        if (!"category".equals(updatedCategory.getType()) && !"organization".equals(updatedCategory.getType())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.updateCategory(id, updatedCategory));
    }

    // DELETE by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
