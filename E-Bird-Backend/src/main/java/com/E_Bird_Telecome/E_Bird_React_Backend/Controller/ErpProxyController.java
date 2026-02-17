


package com.E_Bird_Telecome.E_Bird_React_Backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/erp")
@CrossOrigin(origins = "*")
public class ErpProxyController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // ERP API credentials from application.properties
    @Value("${erp.api.base-url:https://oneidentitytest.slt.com.lk/ERPAPIs/api/ERPData}")
    private String erpBaseUrl;

    @Value("${erp.api.username:dpuser}")
    private String erpUsername;

    @Value("${erp.api.password:dp@123#}")
    private String erpPassword;

    public ErpProxyController() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Get all organizations
     * GET /api/erp/organizations
     */
    @GetMapping("/organizations")
    public ResponseEntity<?> getOrganizations() {
        try {
            HttpHeaders headers = createErpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpBaseUrl + "/GetOrganizationList",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch organizations: " + e.getMessage()));
        }
    }

    /**
     * Get cost centers for organization
     * POST /api/erp/cost-centers
     */
    @PostMapping("/cost-centers")
    public ResponseEntity<?> getCostCenters(@RequestBody Map<String, String> request) {
        try {
            HttpHeaders headers = createErpHeaders();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpBaseUrl + "/GetCostCentersforOrganizations",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch cost centers: " + e.getMessage()));
        }
    }

    /**
     * Get employee list
     * POST /api/erp/employees
     */
    @PostMapping("/employees")
    public ResponseEntity<?> getEmployees(@RequestBody Map<String, String> request) {
        try {
            HttpHeaders headers = createErpHeaders();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpBaseUrl + "/GetEmployeeList",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch employees: " + e.getMessage()));
        }
    }

    /**
     * Get employee hierarchy
     * POST /api/erp/employee-hierarchy
     */
    @PostMapping("/employee-hierarchy")
    public ResponseEntity<?> getEmployeeHierarchy(@RequestBody Map<String, String> request) {
        try {
            HttpHeaders headers = createErpHeaders();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpBaseUrl + "/GetEmployeeDetailsHierarchy",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch employee hierarchy: " + e.getMessage()));
        }
    }

    /**
     * ✅ NEW: Get employee subordinates details
     * POST /api/erp/employee-subordinates
     */
    @PostMapping("/employee-subordinates")
    public ResponseEntity<?> getEmployeeSubordinates(@RequestBody Map<String, String> request) {
        try {
            System.out.println("🔍 Fetching subordinates for employee: " + request.get("employeeNo"));

            HttpHeaders headers = createErpHeaders();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpBaseUrl + "/GetEmployeeSubordinatesDetailsList",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println("✅ Subordinates response: " + response.getBody());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (Exception e) {
            System.err.println("❌ Error fetching subordinates: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch employee subordinates: " + e.getMessage()));
        }
    }

    /**
     * Public endpoint for employee login (no authentication required)
     * POST /api/erp/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> employeeLogin(@RequestBody Map<String, String> request) {
        try {
            HttpHeaders headers = createErpHeaders();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpBaseUrl + "/GetEmployeeDetailsHierarchy",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to authenticate employee: " + e.getMessage()));
        }
    }

    /**
     * Create HTTP headers for ERP API requests
     */
    private HttpHeaders createErpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "text/plain");
        headers.set("UserName", erpUsername);
        headers.set("Password", erpPassword);
        return headers;
    }
}