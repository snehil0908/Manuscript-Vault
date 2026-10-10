package com.manuscriptvault.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Manuscript Entity - Maps to the 'manuscripts' table in MySQL.
 * Stores manuscript metadata including title, author, date, and description.
 */
@Entity
@Table(name = "manuscripts")
public class Manuscript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @NotBlank(message = "Author is required")
    @Column(name = "author", nullable = false, length = 150)
    private String author;

    @NotNull(message = "Date is required")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "genre", length = 100)
    private String genre;

    @Column(name = "language", length = 50)
    private String language;

    @Column(name = "pages")
    private Integer pages;

    // ---- Constructors ----
    public Manuscript() {}

    public Manuscript(String title, String author, LocalDate date, String description) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.description = description;
    }

    // ---- Getters & Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }

    @Override
    public String toString() {
        return "Manuscript{id=" + id + ", title='" + title + "', author='" + author + "', date=" + date + "}";
    }
}
