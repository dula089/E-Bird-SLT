package com.E_Bird_Telecome.E_Bird_React_Backend.Controller;

import com.E_Bird_Telecome.E_Bird_React_Backend.Model.AddNewRequest;
import com.E_Bird_Telecome.E_Bird_React_Backend.Repository.AddNewRequestRepository;
import com.E_Bird_Telecome.E_Bird_React_Backend.Service.AddNewRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/AddNewRequest")
@CrossOrigin(origins = "*")
public class AddNewRequestController {

    @Autowired
    private AddNewRequestService service;
    
    @Autowired
    private AddNewRequestRepository repository;

    @PostMapping
    public ResponseEntity<AddNewRequest> createRequest(@RequestBody AddNewRequest request, @AuthenticationPrincipal Jwt jwt) {
        long startTime = System.currentTimeMillis();

        // Handle both Azure and ERP users
        if (jwt != null) {
            request.setAssignedBy(jwt.getClaim("name"));
        } else if (request.getAssignedBy() == null || request.getAssignedBy().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        AddNewRequest savedRequest = service.createRequest(request);
        
        long endTime = System.currentTimeMillis();
        System.out.println("✅ Request created in " + (endTime - startTime) + "ms");
        
        return ResponseEntity.ok(savedRequest);
    }

    @PostMapping("/addNewRequest")
    public ResponseEntity<AddNewRequest> addNewRequest(@RequestBody AddNewRequest request, @AuthenticationPrincipal Jwt jwt) {
        long startTime = System.currentTimeMillis();

        if (jwt != null) {
            request.setAssignedBy(jwt.getClaim("name"));
        } else if (request.getAssignedBy() == null || request.getAssignedBy().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        AddNewRequest savedRequest = service.createRequest(request);
        
        long endTime = System.currentTimeMillis();
        System.out.println("✅ Request added in " + (endTime - startTime) + "ms");
        
        return ResponseEntity.ok(savedRequest);
    }

    @GetMapping("/generateRequestId")
    public ResponseEntity<String> generateRequestId() {
        return ResponseEntity.ok(service.generateRequestId());
    }

    /**
     * Get all requests with pagination
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assignedBy) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Create pageable with sorting
            Pageable pageable = PageRequest.of(page, size, Sort.by("receivedDate").descending());
            
            Page<AddNewRequest> pageRequests;
            
            if (status != null && !status.isEmpty()) {
                pageRequests = repository.findByStatus(status, pageable);
            } else if (assignedBy != null && !assignedBy.isEmpty()) {
                pageRequests = repository.findByAssignedBy(assignedBy, pageable);
            } else {
                pageRequests = repository.findAll(pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("requests", pageRequests.getContent());
            response.put("currentPage", pageRequests.getNumber());
            response.put("totalItems", pageRequests.getTotalElements());
            response.put("totalPages", pageRequests.getTotalPages());
            response.put("pageSize", pageRequests.getSize());
            response.put("hasNext", pageRequests.hasNext());
            response.put("hasPrevious", pageRequests.hasPrevious());
            
            long endTime = System.currentTimeMillis();
            System.out.println("✅ Fetched " + pageRequests.getNumberOfElements() + " requests in " + (endTime - startTime) + "ms");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching requests: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch requests");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("requests", new ArrayList<>());
            errorResponse.put("totalItems", 0);
            errorResponse.put("totalPages", 0);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddNewRequest> getRequestById(@PathVariable String id) {
        Optional<AddNewRequest> request = service.getRequestById(id);
        return request.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddNewRequest> updateRequest(@PathVariable String id, @RequestBody AddNewRequest updatedRequest) {
        long startTime = System.currentTimeMillis();
        
        AddNewRequest updated = service.updateRequest(id, updatedRequest);
        
        long endTime = System.currentTimeMillis();
        System.out.println("✅ Request updated in " + (endTime - startTime) + "ms");
        
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable String id) {
        long startTime = System.currentTimeMillis();
        
        service.deleteRequest(id);
        
        long endTime = System.currentTimeMillis();
        System.out.println("✅ Request deleted in " + (endTime - startTime) + "ms");
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRequestStats() {
        long startTime = System.currentTimeMillis();
        
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", repository.count());
            stats.put("pending", repository.countByStatus("Pending"));
            stats.put("inProgress", repository.countByStatus("In Progress"));
            stats.put("completed", repository.countByStatus("Completed"));
            stats.put("rejected", repository.countByStatus("Rejected"));
            stats.put("underReview", repository.countByStatus("Under Review"));
            stats.put("approved", repository.countByStatus("Approved"));
            
            long endTime = System.currentTimeMillis();
            System.out.println("✅ Stats fetched in " + (endTime - startTime) + "ms");
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching stats: " + e.getMessage());
            return ResponseEntity.status(500).body(new HashMap<>());
        }
    }
}