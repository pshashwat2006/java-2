package com.kiet.examproject.model;

/**
 * ============================================================================
 * MODEL LAYER: Book Entity
 * ============================================================================
 * 
 * VIVA EXPLANATION:
 * 1. What is a Model in MVC?
 *    The Model represents the application's domain data and state.
 *    It encapsulates the entity attributes and provides public accessors
 *    (getters and setters) according to the JavaBean specification.
 * 
 * 2. Why are there NO JPA / Hibernate annotations here?
 *    This project uses Spring JDBC (JdbcTemplate) and raw JDBC rather than an ORM.
 *    Plain Old Java Objects (POJOs) are used directly with SQL RowMappers, avoiding
 *    heavy ORM overhead and satisfying the academic syllabus requirement.
 * 
 * Fields according to requirements:
 * - id: Integer (Primary Key, Auto-incremented by MySQL)
 * - title: String (Book title)
 * - author: String (Author name)
 * - category: String (Genre / Department category)
 * - availableCopies: Integer (Number of copies currently available in library)
 */
public class Book {

    private Integer id;
    private String title;
    private String author;
    private String category;
    private Integer availableCopies;

    // Default No-Argument Constructor (Required for JSON deserialization by Jackson)
    public Book() {
    }

    // Parameterized Constructor without ID (Used when inserting a new book before MySQL assigns an ID)
    public Book(String title, String author, String category, Integer availableCopies) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.availableCopies = availableCopies;
    }

    // Parameterized Constructor with ID (Used when mapping database rows to Java objects)
    public Book(Integer id, String title, String author, String category, Integer availableCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.availableCopies = availableCopies;
    }

    // --- Getters and Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", availableCopies=" + availableCopies +
                '}';
    }
}
