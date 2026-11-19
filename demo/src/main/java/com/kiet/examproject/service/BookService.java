package com.kiet.examproject.service;

import com.kiet.examproject.config.AppConfig;
import com.kiet.examproject.dao.BookDao;
import com.kiet.examproject.model.Book;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * ============================================================================
 * SERVICE LAYER: BookService
 * ============================================================================
 * 
 * VIVA EXPLANATION:
 * 1. What is the Service Layer?
 *    The Service layer houses business logic, data validation, calculation, and
 *    coordinates between the Controller (presentation) and DAO (persistence).
 * 
 * 2. What is @Service?
 *    A Spring stereotype annotation indicating this class performs business services.
 *    It is automatically detected during component scanning and registered in the IoC container.
 * 
 * 3. What is Inversion of Control (IoC) & Dependency Injection (DI)?
 *    - IoC: Instead of a class instantiating its own dependencies using 'new', control
 *      over object lifecycle and wiring is inverted and delegated to the Spring Container.
 *    - DI: The design pattern where dependencies are "injected" into a component at runtime.
 * 
 * 4. Constructor Injection vs Field Injection:
 *    - CONSTRUCTOR INJECTION (Primary & Recommended):
 *      Dependencies are passed through the constructor. Guarantees immutability (final fields),
 *      prevents NullPointerException, facilitates easy unit testing with mocks, and makes
 *      required dependencies explicit.
 *    - FIELD INJECTION (@Autowired on fields):
 *      Spring uses Java reflection to inject fields directly. It is concise, but hides
 *      dependencies, prevents immutability, and makes standalone unit testing harder.
 * 
 * 5. Lifecycle Callbacks:
 *    - @PostConstruct: Executed immediately after the bean's properties are injected and dependencies
 *      are resolved, before the bean is put into service.
 *    - @PreDestroy: Executed right before the bean is destroyed when the Spring ApplicationContext shuts down.
 */
@Service
public class BookService {

    // PRIMARY DEPENDENCY INJECTION: Constructor Injection (Final & Immutable)
    private final BookDao bookDao;

    // ObjectProvider to dynamically retrieve new Prototype bean instances from Spring container
    private final ObjectProvider<AppConfig.BookAuditHelper> auditHelperProvider;

    // ========================================================================
    // SYLLABUS INSPECTION: Field Injection Demonstration
    // ========================================================================
    @Autowired
    private BookDao fieldInjectedBookDaoForVivaDemo;

    /**
     * PRIMARY CONSTRUCTOR INJECTION:
     * Spring automatically resolves and supplies BookDao and ObjectProvider<BookAuditHelper>.
     */
    public BookService(BookDao bookDao, ObjectProvider<AppConfig.BookAuditHelper> auditHelperProvider) {
        this.bookDao = bookDao;
        this.auditHelperProvider = auditHelperProvider;
        System.out.println("[BookService] Initialized with Constructor Injection.");
    }

    // ========================================================================
    // BEAN LIFECYCLE CALLBACK METHODS (@PostConstruct & @PreDestroy)
    // ========================================================================

    /**
     * Post-Initialization Callback:
     * Called automatically by Spring after dependency injection is complete.
     */
    @PostConstruct
    public void init() {
        System.out.println("[LIFECYCLE CALLBACK - @PostConstruct] BookService bean initialized and ready for requests!");
        if (fieldInjectedBookDaoForVivaDemo != null) {
            System.out.println("[DI DEMO] @Autowired Field Injection successfully verified: fieldInjectedBookDaoForVivaDemo is active.");
        }
    }

    /**
     * Pre-Destruction Callback:
     * Called automatically by Spring right before the application context shuts down.
     */
    @PreDestroy
    public void cleanup() {
        System.out.println("[LIFECYCLE CALLBACK - @PreDestroy] BookService bean is being destroyed. Releasing resources.");
    }

    // ========================================================================
    // BUSINESS LOGIC METHODS
    // ========================================================================

    /**
     * Fetches all books from the database.
     */
    public List<Book> getAllBooks() {
        return bookDao.findAll();
    }

    /**
     * Fetches a specific book by ID.
     */
    public Book getBookById(int id) {
        return bookDao.findById(id);
    }

    /**
     * Creates a new book after validation, records audit log via prototype bean, and persists.
     */
    public Book addBook(Book book) {
        // Business Validation
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty.");
        }
        if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        if (book.getAvailableCopies() == null || book.getAvailableCopies() < 0) {
            throw new IllegalArgumentException("Available copies must be zero or a positive integer.");
        }

        // Prototype Scope Demonstration: fetch a fresh instance for this specific action
        AppConfig.BookAuditHelper auditHelper = auditHelperProvider.getObject();
        String auditMessage = auditHelper.auditAction("ADD_BOOK", book.getTitle());
        System.out.println(auditMessage);

        return bookDao.save(book);
    }

    /**
     * Updates an existing book after validation and existence check.
     */
    public Book updateBook(int id, Book book) {
        // Check if book exists
        Book existing = bookDao.findById(id);
        if (existing == null) {
            return null; // Return null so controller can return 404 Not Found
        }

        // Business Validation
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty.");
        }
        if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        if (book.getAvailableCopies() == null || book.getAvailableCopies() < 0) {
            throw new IllegalArgumentException("Available copies must be zero or a positive integer.");
        }

        // Prototype Scope Demonstration: fetch fresh instance
        AppConfig.BookAuditHelper auditHelper = auditHelperProvider.getObject();
        String auditMessage = auditHelper.auditAction("UPDATE_BOOK", book.getTitle());
        System.out.println(auditMessage);

        int rows = bookDao.update(id, book);
        if (rows > 0) {
            book.setId(id);
            return book;
        }
        return null;
    }

    /**
     * Deletes a book by ID. Returns true if deleted, false if not found.
     */
    public boolean deleteBook(int id) {
        Book existing = bookDao.findById(id);
        if (existing == null) {
            return false;
        }

        AppConfig.BookAuditHelper auditHelper = auditHelperProvider.getObject();
        String auditMessage = auditHelper.auditAction("DELETE_BOOK", existing.getTitle());
        System.out.println(auditMessage);

        int rows = bookDao.deleteById(id);
        return rows > 0;
    }

    /**
     * Fetches a book using the Raw JDBC demonstration method.
     */
    public Book getBookByIdRawJdbc(int id) throws SQLException, ClassNotFoundException {
        return bookDao.findByIdRawJdbc(id);
    }

    /**
     * Prototype Bean inspection for the REST API demonstration.
     */
    public AppConfig.BookAuditHelper getFreshAuditHelperInstance() {
        return auditHelperProvider.getObject();
    }
}
