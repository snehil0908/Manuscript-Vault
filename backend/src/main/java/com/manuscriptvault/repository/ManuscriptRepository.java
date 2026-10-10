package com.manuscriptvault.repository;

import com.manuscriptvault.model.Manuscript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * ManuscriptRepository - JPA repository for Manuscript entity.
 * Provides CRUD + custom search/filter operations.
 */
@Repository
public interface ManuscriptRepository extends JpaRepository<Manuscript, Long> {

    /**
     * Search manuscripts by title (case-insensitive partial match).
     */
    List<Manuscript> findByTitleContainingIgnoreCase(String title);

    /**
     * Search manuscripts by author name (case-insensitive partial match).
     */
    List<Manuscript> findByAuthorContainingIgnoreCase(String author);

    /**
     * Find manuscripts within a date range.
     */
    List<Manuscript> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Global search across title, author, and description.
     */
    @Query("SELECT m FROM Manuscript m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Manuscript> searchByQuery(@Param("query") String query);

    /**
     * Filter by genre.
     */
    List<Manuscript> findByGenreIgnoreCase(String genre);

    /**
     * Find all manuscripts ordered by date descending.
     */
    List<Manuscript> findAllByOrderByDateDesc();
}
