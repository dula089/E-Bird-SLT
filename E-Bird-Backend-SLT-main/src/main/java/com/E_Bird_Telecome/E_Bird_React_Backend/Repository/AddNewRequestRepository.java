package com.E_Bird_Telecome.E_Bird_React_Backend.Repository;

import com.E_Bird_Telecome.E_Bird_React_Backend.Model.AddNewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AddNewRequestRepository extends MongoRepository<AddNewRequest, String> {
    long countByRequestIdStartingWith(String prefix);
    
    // Optimized queries with specific field projections
    @Query(value = "{}", fields = "{ 'requestId' : 1, 'receivedDate' : 1, 'assignedBy' : 1, 'mainCategory' : 1, 'assignTo' : 1, 'assignToName' : 1, 'status' : 1, 'forwardedBy' : 1, 'attachments' : 1, 'forwardingHistory' : 1 }")
    Page<AddNewRequest> findAllProjected(Pageable pageable);
    
    @Query(value = "{ 'status' : ?0 }", fields = "{ 'requestId' : 1, 'receivedDate' : 1, 'assignedBy' : 1, 'mainCategory' : 1, 'assignTo' : 1, 'assignToName' : 1, 'status' : 1, 'forwardedBy' : 1, 'attachments' : 1, 'forwardingHistory' : 1 }")
    Page<AddNewRequest> findByStatusProjected(String status, Pageable pageable);
    
    @Query(value = "{ 'assignedBy' : ?0 }", fields = "{ 'requestId' : 1, 'receivedDate' : 1, 'assignedBy' : 1, 'mainCategory' : 1, 'assignTo' : 1, 'assignToName' : 1, 'status' : 1, 'forwardedBy' : 1, 'attachments' : 1, 'forwardingHistory' : 1 }")
    Page<AddNewRequest> findByAssignedByProjected(String assignedBy, Pageable pageable);
    
    // Pagination methods
    Page<AddNewRequest> findAll(Pageable pageable);
    Page<AddNewRequest> findByStatus(String status, Pageable pageable);
    Page<AddNewRequest> findByAssignedBy(String assignedBy, Pageable pageable);
    
    // Count methods for stats
    long countByStatus(String status);
}