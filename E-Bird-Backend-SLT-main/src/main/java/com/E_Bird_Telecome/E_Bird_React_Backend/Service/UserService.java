//package com.E_Bird_Telecome.E_Bird_React_Backend.Service;
//
//
////this is code to test AssignTo field works - with add dataSeeder code
//import com.E_Bird_Telecome.E_Bird_React_Backend.Model.User;
//import com.E_Bird_Telecome.E_Bird_React_Backend.Repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository repository;
//
//    public List<User> getActiveUsers() {
//        return repository.findByIsActiveTrue();
//    }
//
//    public User createOrUpdateUser(User user) {
//        User existingUser = repository.findByEmail(user.getEmail());
//        if (existingUser != null) {
//            existingUser.setName(user.getName());
//            existingUser.setDesignation(user.getDesignation());
//            existingUser.setDepartment(user.getDepartment());
//            return repository.save(existingUser);
//        }
//        return repository.save(user);
//    }
//
//    public User getUserByAzureId(String azureObjectId) {
//        return repository.findByAzureObjectId(azureObjectId);
//    }
//}
//






// / CODE FOR -   Auto-Register Users on First Login (want to CHANGE UserController,UserService,frontend AuthUtils.jsx)
package com.E_Bird_Telecome.E_Bird_React_Backend.Service;

import com.E_Bird_Telecome.E_Bird_React_Backend.Model.User;
import com.E_Bird_Telecome.E_Bird_React_Backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public List<User> getActiveUsers() {
        return repository.findByIsActiveTrue();
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User createOrUpdateUser(User user) {
        User existingUser = repository.findByEmail(user.getEmail());
        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setDesignation(user.getDesignation());
            existingUser.setDepartment(user.getDepartment());
            existingUser.setActive(user.isActive());
            existingUser.setAzureObjectId(user.getAzureObjectId());
            return repository.save(existingUser);
        }
        return repository.save(user);
    }

    public User getUserByAzureId(String azureObjectId) {
        return repository.findByAzureObjectId(azureObjectId);
    }

    public User getUserById(String id) {
        return repository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return repository.findByEmail(email);
    }
}