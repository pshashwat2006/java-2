package com.kiet.examproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================================
 * MAIN APPLICATION ENTRY POINT: ExamprojectApplication
 * ============================================================================
 * 
 * VIVA EXPLANATION:
 * 1. What is @SpringBootApplication?
 *    It is a composite annotation that bundles three essential Spring annotations:
 *    - @Configuration: Identifies this class as a configuration source for Spring beans.
 *    - @EnableAutoConfiguration: Directs Spring Boot to automatically configure
 *      beans based on the classpath libraries (e.g., DataSource, JdbcTemplate, DispatcherServlet).
 *    - @ComponentScan: Automatically scans the current package ("com.kiet.examproject")
 *      and its sub-packages (model, dao, service, controller, config, aspect) to discover
 *      and register Spring-managed components (@Component, @Service, @Repository, @RestController).
 * 
 * 2. SpringApplication.run():
 *    Bootstraps the embedded web server (Tomcat by default on port 8080),
 *    creates the ApplicationContext (Spring IoC Container), loads bean definitions,
 *    and wires all dependencies.
 */
@SpringBootApplication
public class ExamprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamprojectApplication.class, args);
        System.out.println("==========================================================");
        System.out.println("  Library Management System REST API is RUNNING!");
        System.out.println("  Admin Dashboard: http://localhost:8080/");
        System.out.println("  REST Endpoint:   http://localhost:8080/api/books");
        System.out.println("==========================================================");
    }
}
