//package com.E_Bird_Telecome.E_Bird_React_Backend.Config;
//
//
//import com.E_Bird_Telecome.E_Bird_React_Backend.Model.User;
//import com.E_Bird_Telecome.E_Bird_React_Backend.Repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//public class DataSeeder implements CommandLineRunner {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Check if users already exist
//        if (userRepository.count() == 0) {
//            List<User> testUsers = Arrays.asList(
//                    createUser("Nadini", "nadini.2023062@iit.ac.lk", "Chief Officer"),
//                    createUser("Senior Manager", "manager@test.com", "Senior Manager"),
//                    createUser("IT Support", "support@test.com", "Support Staff"),
//                    createUser("Department Head", "head@test.com", "Department Head"),
//                    createUser("Assistant Manager", "assistant@test.com", "Assistant Manager")
//            );
//
//            userRepository.saveAll(testUsers);
//            System.out.println("✅ Test users seeded successfully!");
//        } else {
//            System.out.println("ℹ️ Users already exist, skipping seed.");
//        }
//    }
//
//    private User createUser(String name, String email, String designation) {
//        User user = new User();
//        user.setName(name);
//        user.setEmail(email);
//        user.setDesignation(designation);
//        user.setActive(true);
//        return user;
//    }
//}