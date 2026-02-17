package com.E_Bird_Telecome.E_Bird_React_Backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@EnableAsync
public class ErpAsyncService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erp.api.base-url:https://oneidentitytest.slt.com.lk/ERPAPIs/api/ERPData}")
    private String erpBaseUrl;

    @Value("${erp.api.username:dpuser}")
    private String erpUsername;

    @Value("${erp.api.password:dp@123#}")
    private String erpPassword;

    private HttpHeaders createErpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "text/plain");
        headers.set("UserName", erpUsername);
        headers.set("Password", erpPassword);
        return headers;
    }

    @Async
    public CompletableFuture<String> fetchOrganizationsAsync() {
        try {
            HttpHeaders headers = createErpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            String result = restTemplate.exchange(
                    erpBaseUrl + "/GetOrganizationList",
                    HttpMethod.GET,
                    entity,
                    String.class
            ).getBody();
            
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<String> fetchCostCentersAsync(Map<String, String> request) {
        try {
            HttpHeaders headers = createErpHeaders();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            
            String result = restTemplate.exchange(
                    erpBaseUrl + "/GetCostCentersforOrganizations",
                    HttpMethod.POST,
                    entity,
                    String.class
            ).getBody();
            
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<String> fetchEmployeesAsync(Map<String, String> request) {
        try {
            HttpHeaders headers = createErpHeaders();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            
            String result = restTemplate.exchange(
                    erpBaseUrl + "/GetEmployeeList",
                    HttpMethod.POST,
                    entity,
                    String.class
            ).getBody();
            
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
