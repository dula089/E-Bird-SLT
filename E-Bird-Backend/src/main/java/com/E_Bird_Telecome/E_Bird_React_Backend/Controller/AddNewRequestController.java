package com.E_Bird_Telecome.E_Bird_React_Backend.Controller;
import com.E_Bird_Telecome.E_Bird_React_Backend.Model.AddNewRequest;
import com.E_Bird_Telecome.E_Bird_React_Backend.Service.AddNewRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/AddNewRequest")
@CrossOrigin(origins = "*")
public class
AddNewRequestController {

    @Autowired
    private AddNewRequestService service;

    @PostMapping
    public ResponseEntity<AddNewRequest> createRequest(@RequestBody AddNewRequest request, @AuthenticationPrincipal Jwt jwt) {

        // Handle both Azure and ERP users
        if (jwt != null) {
            // Azure user - get name from JWT
            request.setAssignedBy(jwt.getClaim("name"));
        } else if (request.getAssignedBy() == null || request.getAssignedBy().isEmpty()) {
            // ERP user - assignedBy should come from frontend
            return ResponseEntity.badRequest().build();
        }

        AddNewRequest savedRequest = service.createRequest(request);
        return ResponseEntity.ok(savedRequest);
    }

    @PostMapping("/addNewRequest")
    public ResponseEntity<AddNewRequest> addNewRequest(@RequestBody AddNewRequest request, @AuthenticationPrincipal Jwt jwt) {

        // Handle both Azure and ERP users
        if (jwt != null) {
            request.setAssignedBy(jwt.getClaim("name"));
        } else if (request.getAssignedBy() == null || request.getAssignedBy().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        AddNewRequest savedRequest = service.createRequest(request);
        return ResponseEntity.ok(savedRequest);
    }

    @GetMapping("/generateRequestId")
    public ResponseEntity<String> generateRequestId() {
        return ResponseEntity.ok(service.generateRequestId());
    }

    @GetMapping
    public ResponseEntity<List<AddNewRequest>> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddNewRequest> getRequestById(@PathVariable String id) {
        Optional<AddNewRequest> request = service.getRequestById(id);
        return request.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddNewRequest> updateRequest(@PathVariable String id, @RequestBody AddNewRequest updatedRequest) {

        AddNewRequest updated = service.updateRequest(id, updatedRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable String id) {
        service.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}