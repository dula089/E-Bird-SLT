package com.E_Bird_Telecome.E_Bird_React_Backend.Controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).body(Map.of("valid", false));
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("valid", true);
        userInfo.put("name", jwt.getClaim("name"));
        userInfo.put("email", jwt.getClaim("preferred_username"));
        userInfo.put("username", jwt.getClaim("preferred_username"));
        userInfo.put("roles", jwt.getClaim("roles"));

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", jwt.getClaim("name"));
        userInfo.put("email", jwt.getClaim("preferred_username"));
        userInfo.put("username", jwt.getClaim("preferred_username"));
        userInfo.put("sub", jwt.getSubject());

        return ResponseEntity.ok(userInfo);
    }
}