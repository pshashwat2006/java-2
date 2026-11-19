-- ============================================================================
-- DATABASE SCHEMA INITIALIZATION: schema.sql
-- ============================================================================
-- 
-- VIVA EXPLANATION:
-- 1. How does schema.sql initialize the database?
--    When Spring Boot starts with 'spring.sql.init.mode=always' in application.properties,
--    the DataSourceInitializer locates schema.sql on the classpath and executes
--    the SQL DDL statements automatically against the configured MySQL database.
-- 
-- 2. Why 'CREATE TABLE IF NOT EXISTS'?
--    Ensures idempotency: running the script multiple times across restarts will not
--    cause SQL errors or overwrite existing data.
-- 
-- 3. Field Mappings:
--    - id -> Integer (Primary Key, Auto-increment)
--    - title -> String (NOT NULL)
--    - author -> String (NOT NULL)
--    - category -> String (NOT NULL)
--    - available_copies -> Integer (NOT NULL, defaults to 0)

CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    available_copies INT NOT NULL DEFAULT 0
);
