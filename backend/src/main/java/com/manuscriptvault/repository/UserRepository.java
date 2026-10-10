package com.manuscriptvault.repository;

import com.manuscriptvault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Spring Data JPA repository for User entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by username (used for authentication).
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a username already exists (used during signup).
     */
    boolean existsByUsername(String username);
}
