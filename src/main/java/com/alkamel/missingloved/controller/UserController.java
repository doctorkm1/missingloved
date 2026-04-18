package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.User;
import com.alkamel.missingloved.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ Create a new user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("خطأ أثناء إنشاء المستخدم: " + e.getMessage());
        }
    }

    // ✅ Retrieve a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("لم يتم العثور على المستخدم بالمعرف: " + id);
        }
    }

    // ✅ Retrieve all users
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("حدث خطأ أثناء استرجاع المستخدمين: " + e.getMessage());
        }
    }

    // ✅ Update an existing user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("المستخدم غير موجود بالمعرف: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("خطأ أثناء تحديث المستخدم: " + e.getMessage());
        }
    }

    // ✅ Get user details in a custom format
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable Long id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Build full name
            String fullName = String.join(" ",
                    user.getFirstName() != null ? user.getFirstName() : "",
                    user.getSecondName() != null ? user.getSecondName() : "",
                    user.getFamilyName() != null ? user.getFamilyName() : ""
            ).trim();

            Map<String, Object> details = Map.of(
                    "id", user.getId(),
                    "كود المستخدم", user.getUserCode() != null ? user.getUserCode() : "",
                    "الإسم", fullName,
                    "الصلاحية", user.getAuthorityCode() != null ? user.getAuthorityCode() : "",
                    "createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
            );

            return ResponseEntity.ok(details);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    // ✅ Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("تم حذف المستخدم بنجاح");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("خطأ أثناء حذف المستخدم: " + e.getMessage());
        }
    }
}
