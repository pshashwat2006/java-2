package com.kiet.examproject.controller;

import com.kiet.examproject.config.AppConfig;
import com.kiet.examproject.model.Book;
import com.kiet.examproject.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * CONTROLLER LAYER: BookController
 * REST API Endpoints for Library Book Management
 * ============================================================================
 * 
 * VIVA EXPLANATION:
 * 1. What is @RestController?
 *    A combination of @Controller and @ResponseBody. It marks this class as a
 *    Spring MVC Controller where every handler method automatically serializes
 *    return objects into JSON HTTP response bodies (via Jackson).
 * 
 * 2. What is @RequestMapping("/api/books")?
 *    Maps all HTTP requests with URL paths starting with "/api/books" to this controller.
 * 
 * 3. Constructor Dependency Injection:
 *    BookService is injected by Spring into this controller's constructor.
 *    The Controller NEVER talks directly to the DAO; it strictly respects MVC layering.
 * 
 * 4. HTTP Status Codes Used:
 *    - 200 OK: Request succeeded (read, update, delete).
 *    - 201 CREATED: New resource successfully created (POST).
 *    - 400 BAD REQUEST: Client provided invalid data or parameters.
 *    - 404 NOT FOUND: Requested entity does not exist in the database.
 *    - 500 INTERNAL SERVER ERROR: Handled gracefully with clean JSON.
 */
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*") // Allows access from web browsers without CORS restrictions
public class BookController {

    private final BookService bookService;

    /**
     * Constructor Injection for BookService
     */
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * c) GET /api/books: Retrieve all books
     * HTTP Method: GET
     * Response: 200 OK with JSON array of books
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    /**
     * c) GET /api/books/{id}: Retrieve a specific book by ID
     * HTTP Method: GET
     * Response: 200 OK with book JSON, or 404 Not Found if missing
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable("id") int id) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Book with ID " + id + " was not found in the library.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * b) POST /api/books: Add a new book
     * HTTP Method: POST
     * Annotation: @RequestBody binds incoming JSON to the Book model
     * Response: 201 Created with created Book, or 400 Bad Request
     */
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        try {
            Book createdBook = bookService.addBook(book);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Book '" + createdBook.getTitle() + "' successfully added!");
            response.put("data", createdBook);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to add book due to server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * d) PUT /api/books/{id}: Update a book's details
     * HTTP Method: PUT
     * Updates title, author, category, and availableCopies
     * Response: 200 OK with updated Book, or 404 Not Found, or 400 Bad Request
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable("id") int id, @RequestBody Book book) {
        try {
            Book updatedBook = bookService.updateBook(id, book);
            if (updatedBook != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Book ID " + id + " updated successfully!");
                response.put("data", updatedBook);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Cannot update. Book with ID " + id + " was not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to update book due to server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * e) DELETE /api/books/{id}: Delete a book by ID
     * HTTP Method: DELETE
     * Response: 200 OK with message, or 404 Not Found if missing
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") int id) {
        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Book with ID " + id + " was successfully deleted from library records.");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Cannot delete. Book with ID " + id + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ========================================================================
    // SYLLABUS DEMONSTRATION ENDPOINTS (Raw JDBC & Prototype Scope)
    // ========================================================================

    /**
     * Raw JDBC Demonstration Endpoint:
     * GET /api/books/raw/{id}
     * Executes the low-level DriverManager + PreparedStatement query.
     */
    @GetMapping("/raw/{id}")
    public ResponseEntity<?> getBookByIdRawJdbc(@PathVariable("id") int id) {
        try {
            Book book = bookService.getBookByIdRawJdbc(id);
            if (book != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("demonstration", "Raw JDBC (DriverManager + PreparedStatement)");
                response.put("data", book);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Raw JDBC: Book with ID " + id + " not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Raw JDBC execution error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Prototype Bean Scope Demonstration Endpoint:
     * GET /api/books/prototype-demo
     * Proves that each request produces a new instance with a distinct ID.
     */
    @GetMapping("/prototype-demo")
    public ResponseEntity<?> prototypeScopeDemo() {
        AppConfig.BookAuditHelper instance1 = bookService.getFreshAuditHelperInstance();
        AppConfig.BookAuditHelper instance2 = bookService.getFreshAuditHelperInstance();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Spring @Scope('prototype') Demonstration");
        response.put("instance1_id", instance1.getInstanceId());
        response.put("instance1_created", instance1.getCreationTime());
        response.put("instance2_id", instance2.getInstanceId());
        response.put("instance2_created", instance2.getCreationTime());
        response.put("isDifferentInstance", instance1 != instance2);
        return ResponseEntity.ok(response);
    }
}
