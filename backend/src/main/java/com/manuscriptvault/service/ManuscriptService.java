package com.manuscriptvault.service;

import com.manuscriptvault.model.Manuscript;
import com.manuscriptvault.repository.ManuscriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * ManuscriptService - Business logic layer for manuscript management.
 * Called by ManuscriptController; interacts with ManuscriptRepository.
 */
@Service
public class ManuscriptService {

    @Autowired
    private ManuscriptRepository manuscriptRepository;

    // ---- CREATE ----

    /**
     * Add a new manuscript to the database.
     */
    public Manuscript addManuscript(Manuscript manuscript) {
        validateManuscript(manuscript);
        return manuscriptRepository.save(manuscript);
    }

    // ---- READ ----

    /**
     * Get all manuscripts, ordered by date (newest first).
     */
    public List<Manuscript> getAllManuscripts() {
        return manuscriptRepository.findAllByOrderByDateDesc();
    }

    /**
     * Get a single manuscript by ID.
     */
    public Manuscript getManuscriptById(Long id) {
        return manuscriptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manuscript not found with id: " + id));
    }

    // ---- UPDATE ----

    /**
     * Update an existing manuscript (all fields).
     */
    public Manuscript updateManuscript(Long id, Manuscript updatedData) {
        Manuscript existing = getManuscriptById(id);

        // Update fields
        existing.setTitle(updatedData.getTitle());
        existing.setAuthor(updatedData.getAuthor());
        existing.setDate(updatedData.getDate());
        existing.setDescription(updatedData.getDescription());
        existing.setGenre(updatedData.getGenre());
        existing.setLanguage(updatedData.getLanguage());
        existing.setPages(updatedData.getPages());

        validateManuscript(existing);
        return manuscriptRepository.save(existing);
    }

    // ---- DELETE ----

    /**
     * Delete a manuscript by ID.
     */
    public void deleteManuscript(Long id) {
        if (!manuscriptRepository.existsById(id)) {
            throw new RuntimeException("Manuscript not found with id: " + id);
        }
        manuscriptRepository.deleteById(id);
    }

    // ---- SEARCH & FILTER ----

    /**
     * Global search across title, author, and description.
     */
    public List<Manuscript> searchManuscripts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllManuscripts();
        }
        return manuscriptRepository.searchByQuery(query.trim());
    }

    /**
     * Search manuscripts by title only.
     */
    public List<Manuscript> searchByTitle(String title) {
        return manuscriptRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Search manuscripts by author only.
     */
    public List<Manuscript> searchByAuthor(String author) {
        return manuscriptRepository.findByAuthorContainingIgnoreCase(author);
    }

    /**
     * Filter manuscripts by date range.
     */
    public List<Manuscript> filterByDateRange(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return manuscriptRepository.findByDateBetween(start, end);
    }

    /**
     * Filter manuscripts by genre.
     */
    public List<Manuscript> filterByGenre(String genre) {
        return manuscriptRepository.findByGenreIgnoreCase(genre);
    }

    // ---- VALIDATION ----

    private void validateManuscript(Manuscript m) {
        if (m.getTitle() == null || m.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Title is required");
        }
        if (m.getAuthor() == null || m.getAuthor().trim().isEmpty()) {
            throw new RuntimeException("Author is required");
        }
        if (m.getDate() == null) {
            throw new RuntimeException("Date is required");
        }
    }
}
