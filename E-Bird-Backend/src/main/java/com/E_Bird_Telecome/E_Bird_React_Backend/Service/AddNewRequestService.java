package com.E_Bird_Telecome.E_Bird_React_Backend.Service;

import com.E_Bird_Telecome.E_Bird_React_Backend.Model.AddNewRequest;
import com.E_Bird_Telecome.E_Bird_React_Backend.Repository.AddNewRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AddNewRequestService {

    @Autowired
    private AddNewRequestRepository repository;

    public String generateRequestId() {
        String year = String.valueOf(LocalDate.now().getYear());
        long count = repository.countByRequestIdStartingWith("REQ-" + year);
        String sequence = String.format("%03d", count + 1);
        return "REQ-" + year + "-" + sequence;
    }


    public AddNewRequest createRequest(AddNewRequest request) {
        String generatedId = generateRequestId();
        request.setRequestId(generatedId);
        request.setStatus("Pending"); // Default status
        return repository.save(request);
    }

    public List<AddNewRequest> getAllRequests() {
        return repository.findAll();
    }

    public Optional<AddNewRequest> getRequestById(String id) {
        return repository.findById(id);
    }

    public AddNewRequest updateRequest(String id, AddNewRequest updatedRequest) {
        updatedRequest.setId(id);
        return repository.save(updatedRequest);
    }

    public void deleteRequest(String id) {
        repository.deleteById(id);
    }
}
