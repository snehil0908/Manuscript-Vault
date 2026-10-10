package com.manuscriptvault.controller;

import com.manuscriptvault.dto.AuthDTOs.ApiResponse;
import com.manuscriptvault.model.Manuscript;
import com.manuscriptvault.service.ManuscriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ManuscriptController - REST API for Manuscript CRUD operations.
 * Admin: full access (create, update, delete, read)
 * User:  read-only access (list, search, filter)
 *
 * Base URL: /api/manuscripts
 */
@RestController
@RequestMapping("/api/manuscripts")
@CrossOrigin(origins = "*")
public class ManuscriptController {

    @Autowired
    private ManuscriptService manuscriptService;

    // ============================
    // READ endpoints (all authenticated users)
    // ============================

    /**
     * GET /api/manuscripts
     * Returns all manuscripts (newest first).
     */
    @GetMapping
    public ResponseEntity<List<Manuscript>> getAllManuscripts() {
        List<Manuscript> manuscripts = manuscriptService.getAllManuscripts();
        return ResponseEntity.ok(manuscripts);
    }

    /**
     * GET /api/manuscripts/{id}
     * Returns a single manuscript by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getManuscriptById(@PathVariable Long id) {
        try {
            Manuscript manuscript = manuscriptService.getManuscriptById(id);
            return ResponseEntity.ok(manuscript);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * GET /api/manuscripts/search?query=...
     * Global search across title, author, description.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Manuscript>> searchManuscripts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {

        List<Manuscript> results;

        if (title != null && !title.isEmpty()) {
            results = manuscriptService.searchByTitle(title);
        } else if (author != null && !author.isEmpty()) {
            results = manuscriptService.searchByAuthor(author);
        } else {
            results = manuscriptService.searchManuscripts(query);
        }

        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/manuscripts/filter?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
     * Filter by date range.
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filterManuscripts(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String genre) {
        try {
            List<Manuscript> results;

            if (startDate != null && endDate != null) {
                results = manuscriptService.filterByDateRange(startDate, endDate);
            } else if (genre != null && !genre.isEmpty()) {
                results = manuscriptService.filterByGenre(genre);
            } else {
                results = manuscriptService.getAllManuscripts();
            }

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid filter parameters: " + e.getMessage()));
        }
    }

    // ============================
    // WRITE endpoints (ADMIN only)
    // ============================

    /**
     * POST /api/manuscripts
     * Create a new manuscript. (Admin only)
     */
    @PostMapping
    public ResponseEntity<?> addManuscript(@RequestBody Manuscript manuscript) {
        try {
            Manuscript created = manuscriptService.addManuscript(manuscript);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * PUT /api/manuscripts/{id}
     * Update an existing manuscript. (Admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateManuscript(@PathVariable Long id,
                                               @RequestBody Manuscript manuscript) {
        try {
            Manuscript updated = manuscriptService.updateManuscript(id, manuscript);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * DELETE /api/manuscripts/{id}
     * Delete a manuscript by ID. (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteManuscript(@PathVariable Long id) {
        try {
            manuscriptService.deleteManuscript(id);
            return ResponseEntity.ok(new ApiResponse(true, "Manuscript deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
