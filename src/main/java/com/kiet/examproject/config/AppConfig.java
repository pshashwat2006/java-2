package com.kiet.examproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * ============================================================================
 * CONFIGURATION LAYER: AppConfig
 * Demonstrating Spring Bean Configuration, Bean Scopes, and Lifecycle
 * ============================================================================
 * 
 * VIVA EXPLANATION:
 * 1. What is @Configuration?
 *    Indicates that this class contains Spring @Bean definitions.
 *    The Spring container processes this class to generate Spring Bean definitions
 *    at runtime.
 * 
 * 2. What is @Bean?
 *    Applied on a method to indicate that it returns a bean to be managed by
 *    the Spring ApplicationContext.
 * 
 * 3. SPRING BEAN SCOPES EXPLANATION:
 *    a) Singleton (DEFAULT): Only ONE instance of the bean is created per Spring IoC container.
 *       All requests for that bean return the identical cached instance.
 *    b) Prototype: A BRAND NEW instance of the bean is created every single time it is
 *       requested from the container. Ideal for stateful objects or temporary processing.
 *    c) Request (Web-aware): Creates a single bean instance per HTTP request lifecycle.
 *    d) Session (Web-aware): Creates a single bean instance per HTTP session lifecycle.
 *    e) Application (Web-aware): Creates a single bean instance per ServletContext lifecycle.
 *    f) WebSocket (Web-aware): Creates a single bean instance per WebSocket session lifecycle.
 */
@Configuration
public class AppConfig {

    /**
     * PROTOTYPE BEAN DEMONSTRATION:
     * Defines a BookAuditHelper bean with Prototype scope.
     * Each call to request this bean from Spring creates an isolated, new instance
     * with its own unique instance ID and timestamp.
     */
    @Bean
    @Scope("prototype")
    public BookAuditHelper bookAuditHelper() {
        return new BookAuditHelper();
    }

    /**
     * Helper utility class that acts as the Prototype Bean.
     * Generates a unique tracking token and timestamp upon instantiation.
     */
    public static class BookAuditHelper {
        private final String instanceId;
        private final String creationTime;

        public BookAuditHelper() {
            // Every time Spring creates a prototype bean, these values are newly generated
            this.instanceId = "AUDIT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.creationTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            System.out.println("[AppConfig Prototype Scope] Created new instance: " + instanceId + " at " + creationTime);
        }

        public String auditAction(String operation, String bookTitle) {
            return String.format("[%s | Created: %s] Processed '%s' for book: \"%s\"",
                    instanceId, creationTime, operation, bookTitle);
        }

        public String getInstanceId() {
            return instanceId;
        }

        public String getCreationTime() {
            return creationTime;
        }
    }
}
