package com.manuscriptvault.controller;

import com.manuscriptvault.dto.AuthDTOs.ApiResponse;
import com.manuscriptvault.model.User;
import com.manuscriptvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * UserController - REST API for admin user management.
 * All endpoints require ADMIN role (enforced via SecurityConfig).
 *
 * Base URL: /api/users
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/users
     * List all registered users. (Admin only)
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // Hide passwords before returning
        users.forEach(u -> u.setPassword("[HIDDEN]"));
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id}
     * Get a specific user by ID. (Admin only)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            user.setPassword("[HIDDEN]");
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * DELETE /api/users/{id}
     * Delete a user by ID. (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * PUT /api/users/{id}/role
     * Update a user's role. (Admin only)
     * Body: { "role": "ADMIN" | "USER" }
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id,
                                             @RequestBody Map<String, String> body) {
        try {
            String newRole = body.get("role");
            User updated = userService.updateUserRole(id, newRole);
            updated.setPassword("[HIDDEN]");
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
