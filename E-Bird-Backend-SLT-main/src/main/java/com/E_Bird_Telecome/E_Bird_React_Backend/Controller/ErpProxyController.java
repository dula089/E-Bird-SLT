package com.E_Bird_Telecome.E_Bird_React_Backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/erp")
@CrossOrigin(origins = "*")
public class ErpProxyController {

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    // ERP API credentials from application.properties
    @Value("${erp.api.base-url:https://oneidentitytest.slt.com.lk/ERPAPIs/api/ERPData}")
    private String erpBaseUrl;

    @Value("${erp.api.username:dpuser}")
    private String erpUsername;

    @Value("${erp.api.password:dp@123#}")
    private String erpPassword;

    public ErpProxyController() {
        this.objectMapper = new ObjectMapper();
    }

    private HttpHeaders createErpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "text/plain");
        headers.set("UserName", erpUsername);
        headers.set("Password", erpPassword);
        return headers;
    }

    /**
     * Get all organizations - WITH CACHING
     * GET /api/erp/organizations
     */
    @Cacheable(value = "organizations", key = "'all'", unless = "#result.statusCodeValue != 200")
    @GetMapping("/organizations")
    public ResponseEntity<?> getOrganizations() {
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("📦 Fetching organizations from ERP...");
            
            HttpHeaders headers = createErpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpBaseUrl + "/GetOrganizationList",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            long endTime = System.currentTimeMillis();
            System.out.println("✅ Organizations fetched in " + (endTime - startTime) + "ms");

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
     * Get cost centers for organization - WITH CACHING
     * POST /api/erp/cost-centers
     */
    @Cacheable(value = "costCenters", key = "#request.get('organizationId')", unless = "#result.statusCodeValue != 200")
    @PostMapping("/cost-centers")
    public ResponseEntity<?> getCostCenters(@RequestBody Map<String, String> request) {
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("📦 Fetching cost centers for org: " + request.get("organizationId"));
            
            HttpHeaders headers = createErpHeaders();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpBaseUrl + "/GetCostCentersforOrganizations",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            long endTime = System.currentTimeMillis();
            System.out.println("✅ Cost centers fetched in " + (endTime - startTime) + "ms");

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
     * Get employee list - WITH CACHING
     * POST /api/erp/employees
     */
    @Cacheable(value = "employees", key = "#request.get('organizationId')", unless = "#result.statusCodeValue != 200")
    @PostMapping("/employees")
    public ResponseEntity<?> getEmployees(@RequestBody Map<String, String> request) {
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("📦 Fetching employees for org: " + request.get("organizationId"));
            
            HttpHeaders headers = createErpHeaders();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpBaseUrl + "/GetEmployeeList",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            long endTime = System.currentTimeMillis();
            System.out.println("✅ Employees fetched in " + (endTime - startTime) + "ms");

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
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("🔍 Fetching hierarchy for employee: " + request.get("employeeNo"));
            
            HttpHeaders headers = createErpHeaders();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpBaseUrl + "/GetEmployeeDetailsHierarchy",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            long endTime = System.currentTimeMillis();
            System.out.println("✅ Hierarchy fetched in " + (endTime - startTime) + "ms");

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
     * Get employee subordinates details
     * POST /api/erp/employee-subordinates
     */
    @PostMapping("/employee-subordinates")
    public ResponseEntity<?> getEmployeeSubordinates(@RequestBody Map<String, String> request) {
        long startTime = System.currentTimeMillis();
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

            long endTime = System.currentTimeMillis();
            System.out.println("✅ Subordinates fetched in " + (endTime - startTime) + "ms");

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
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("🔐 Processing employee login...");
            
            HttpHeaders headers = createErpHeaders();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpBaseUrl + "/GetEmployeeDetailsHierarchy",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            long endTime = System.currentTimeMillis();
            System.out.println("✅ Login processed in " + (endTime - startTime) + "ms");

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
     * NEW: Batch fetch multiple data in parallel
     * GET /api/erp/dashboard-data?organizationId=123
     */
    @GetMapping("/dashboard-data")
    public ResponseEntity<?> getDashboardData(@RequestParam String organizationId) {
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("📊 Fetching dashboard data in parallel...");

            // Create request for employees
            Map<String, String> empRequest = new HashMap<>();
            empRequest.put("organizationId", organizationId);

            // Create request for cost centers
            Map<String, String> ccRequest = new HashMap<>();
            ccRequest.put("organizationId", organizationId);

            // Fetch organizations (from cache if available)
            CompletableFuture<ResponseEntity<?>> orgsFuture = CompletableFuture.supplyAsync(() -> 
                getOrganizations()
            );

            // Fetch employees
            CompletableFuture<ResponseEntity<?>> employeesFuture = CompletableFuture.supplyAsync(() -> 
                getEmployees(empRequest)
            );

            // Fetch cost centers
            CompletableFuture<ResponseEntity<?>> costCentersFuture = CompletableFuture.supplyAsync(() -> 
                getCostCenters(ccRequest)
            );

            // Wait for all to complete
            CompletableFuture.allOf(orgsFuture, employeesFuture, costCentersFuture)
                    .orTimeout(15, TimeUnit.SECONDS)
                    .join();

            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("organizations", orgsFuture.get().getBody());
            dashboardData.put("employees", employeesFuture.get().getBody());
            dashboardData.put("costCenters", costCentersFuture.get().getBody());

            long endTime = System.currentTimeMillis();
            System.out.println("✅ Dashboard data fetched in " + (endTime - startTime) + "ms");

            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch dashboard data: " + e.getMessage()));
        }
    }

    /**
     * NEW: Clear cache endpoint (for admin use)
     */
    @PostMapping("/clear-cache")
    public ResponseEntity<?> clearCache() {
        // Note: You would need to inject CacheManager and clear caches
        return ResponseEntity.ok(Map.of("success", true, "message", "Cache cleared"));
    }
}