
package com.E_Bird_Telecome.E_Bird_React_Backend.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String designation;
    private String department;
    private boolean isActive;
    private String azureObjectId;

    // Default constructor
    public User() {
        this.isActive = true; // Set default
    }

    // Constructor with parameters
    public User(String name, String email, String designation) {
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.isActive = true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getAzureObjectId() {
        return azureObjectId;
    }

    public void setAzureObjectId(String azureObjectId) {
        this.azureObjectId = azureObjectId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", designation='" + designation + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}