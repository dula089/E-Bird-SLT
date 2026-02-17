package com.E_Bird_Telecome.E_Bird_React_Backend.Controller;

import com.E_Bird_Telecome.E_Bird_React_Backend.Model.User;
import com.E_Bird_Telecome.E_Bird_React_Backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//
//@RestController
//@RequestMapping("/api/users")
//@CrossOrigin(origins = "*")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/assignable")
//    public ResponseEntity<List<User>> getAssignableUsers() {
//        return ResponseEntity.ok(userService.getActiveUsers());
//    }
//
//    @PostMapping
//    public ResponseEntity<User> createUser(@RequestBody User user) {
//        return ResponseEntity.ok(userService.createOrUpdateUser(user));
//    }
//
//    @GetMapping("/me")
//    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
//        String azureObjectId = jwt.getClaim("oid");
//        User user = userService.getUserByAzureId(azureObjectId);
//
//        // If user doesn't exist, create from JWT claims
//        if (user == null) {
//            user = new User();
//            user.setName(jwt.getClaim("name"));
//            user.setEmail(jwt.getClaim("preferred_username"));
//            user.setAzureObjectId(azureObjectId);
//            user = userService.createOrUpdateUser(user);
//        }
//
//        return ResponseEntity.ok(user);
//    }
//}
//



// / CODE FOR -   Auto-Register Users on First Login (want to CHANGE UserController,UserService,frontend AuthUtils.jsx)
//package com.E_Bird_Telecome.E_Bird_React_Backend.Controller;

import com.E_Bird_Telecome.E_Bird_React_Backend.Model.User;
import com.E_Bird_Telecome.E_Bird_React_Backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

        import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ This endpoint auto-registers user on first login
    @PostMapping("/register-or-login")
    public ResponseEntity<User> registerOrLogin(@AuthenticationPrincipal Jwt jwt) {
        String azureObjectId = jwt.getClaim("oid");
        String name = jwt.getClaim("name");
        String email = jwt.getClaim("preferred_username");

        User user = userService.getUserByAzureId(azureObjectId);

        if (user == null) {
            // First time login - create new user
            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setAzureObjectId(azureObjectId);
            user.setDesignation("Staff"); // Default designation
            user.setActive(true);
            user = userService.createOrUpdateUser(user);

            System.out.println("✅ New user registered: " + name + " (" + email + ")");
        } else {
            // Update last login info if needed
            user.setName(name); // Update name in case it changed
            user = userService.createOrUpdateUser(user);

            System.out.println("ℹ️ Existing user logged in: " + name);
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String azureObjectId = jwt.getClaim("oid");
        User user = userService.getUserByAzureId(azureObjectId);

        if (user == null) {
            // Auto-register if somehow missed
            String name = jwt.getClaim("name");
            String email = jwt.getClaim("preferred_username");

            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setAzureObjectId(azureObjectId);
            user.setDesignation("Staff");
            user.setActive(true);
            user = userService.createOrUpdateUser(user);
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/assignable")
    public ResponseEntity<List<User>> getAssignableUsers() {
        List<User> users = userService.getActiveUsers();
        System.out.println("📋 Fetching assignable users. Count: " + users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Admin endpoint to update user designation/status
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {

        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (updates.containsKey("designation")) {
            user.setDesignation((String) updates.get("designation"));
        }
        if (updates.containsKey("isActive")) {
            user.setActive((Boolean) updates.get("isActive"));
        }
        if (updates.containsKey("department")) {
            user.setDepartment((String) updates.get("department"));
        }

        user = userService.createOrUpdateUser(user);
        return ResponseEntity.ok(user);
    }
}