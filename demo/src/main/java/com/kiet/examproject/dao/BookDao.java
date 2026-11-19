package com.kiet.examproject.dao;

import com.kiet.examproject.model.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * ============================================================================
 * DATA ACCESS OBJECT (DAO) LAYER: BookDao
 * ============================================================================
 * 
 * VIVA EXPLANATION:
 * 1. What is a DAO?
 *    The DAO (Data Access Object) pattern abstracts and encapsulates all access
 *    to the data source (MySQL). It isolates the persistence logic from the business
 *    logic (Service layer).
 * 
 * 2. What is @Repository?
 *    A Spring stereotype annotation that marks this class as a Data Access Component.
 *    It automatically registers the class as a Spring Bean in the IoC container and
 *    enables automatic translation of database-specific exceptions (e.g., SQLExceptions)
 *    into Spring's unchecked DataAccessException hierarchy.
 * 
 * 3. What is JdbcTemplate?
 *    JdbcTemplate is Spring's core JDBC abstraction class. It simplifies relational
 *    database operations by managing connections, creating statements, executing queries,
 *    and closing resources, thereby eliminating repetitive boilerplate code.
 * 
 * 4. What is a RowMapper?
 *    RowMapper is a Spring JDBC interface used to map each row of a ResultSet to a domain object.
 *    Here, lambda (rs, rowNum) -> new Book(...) implements RowMapper<Book>.
 */
@Repository
public class BookDao {

    // Spring's central class for JDBC database interactions
    private final JdbcTemplate jdbcTemplate;

    // Database connection properties injected from application.properties for Raw JDBC demonstration
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    /**
     * CONSTRUCTOR INJECTION:
     * Recommended Spring DI pattern. JdbcTemplate is supplied by Spring Boot auto-configuration.
     */
    public BookDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * RowMapper to convert a MySQL record into a Java Book model instance.
     */
    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("category"),
            rs.getInt("available_copies")
    );

    // ========================================================================
    // PRIMARY CRUD OPERATIONS USING SPRING JdbcTemplate
    // ========================================================================

    /**
     * A. INSERT: Adds a new book to the MySQL database.
     * Uses GeneratedKeyHolder to retrieve the auto-generated primary key (ID).
     */
    public Book save(Book book) {
        String sql = "INSERT INTO books (title, author, category, available_copies) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getAvailableCopies());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            book.setId(keyHolder.getKey().intValue());
        }
        return book;
    }

    /**
     * B. SELECT ALL: Retrieves all book records from the database.
     */
    public List<Book> findAll() {
        String sql = "SELECT id, title, author, category, available_copies FROM books ORDER BY id DESC";
        return jdbcTemplate.query(sql, bookRowMapper);
    }

    /**
     * C. SELECT BY ID: Retrieves a specific book record by its primary key.
     * Returns null if no record is found (handling EmptyResultDataAccessException).
     */
    public Book findById(int id) {
        String sql = "SELECT id, title, author, category, available_copies FROM books WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, bookRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null; // Record not found
        }
    }

    /**
     * D. UPDATE BY ID: Updates the details of an existing book.
     * Returns the number of affected rows (1 if updated, 0 if not found).
     */
    public int update(int id, Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, category = ?, available_copies = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getAvailableCopies(),
                id);
    }

    /**
     * E. DELETE BY ID: Removes a book record by its primary key.
     * Returns the number of affected rows (1 if deleted, 0 if not found).
     */
    public int deleteById(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // ========================================================================
    // F. RAW JDBC DEMONSTRATION (Mandatory Syllabus Requirement)
    // ========================================================================

    /**
     * RAW JDBC HELPER METHOD:
     * Explicitly demonstrates low-level JDBC concepts without Spring abstractions:
     * 1. Class.forName() - Dynamically loads and registers the MySQL JDBC Driver class.
     * 2. DriverManager.getConnection() - Establishes a physical socket connection to MySQL.
     * 3. Connection.prepareStatement() - Pre-compiles the parameterized SQL query.
     * 4. PreparedStatement.setInt() - Binds input parameter safely against SQL injection.
     * 5. PreparedStatement.executeQuery() - Executes the query and returns a ResultSet cursor.
     * 6. ResultSet.next() - Moves cursor forward to extract column values.
     * 7. Try-with-resources - Guarantees safe closing of Connection, Statement, and ResultSet.
     * 
     * COMPARISON FOR VIVA:
     * - Raw JDBC requires manual connection opening/closing, resource leaks handling,
     *   and catching checked SQLException.
     * - Spring's JdbcTemplate eliminates all this boilerplate, translates exceptions,
     *   and manages database connection pools automatically.
     */
    public Book findByIdRawJdbc(int id) throws ClassNotFoundException, SQLException {
        // Step 1: Explicitly load the MySQL JDBC Driver into the JVM
        Class.forName("com.mysql.cj.jdbc.Driver");

        String sql = "SELECT id, title, author, category, available_copies FROM books WHERE id = ?";

        // Step 2 & 3: Establish Connection and create PreparedStatement using try-with-resources
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Step 4: Bind the parameter to placeholder index 1
            preparedStatement.setInt(1, id);

            // Step 5: Execute query and iterate over the ResultSet cursor
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Book(
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getString("category"),
                            resultSet.getInt("available_copies")
                    );
                }
            }
        }
        return null; // Return null if not found
    }
}
