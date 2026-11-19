package com.kiet.examproject.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * ============================================================================
 * ASPECT-ORIENTED PROGRAMMING (AOP) LAYER: LoggingAspect
 * ============================================================================
 * 
 * VIVA EXPLANATION:
 * 1. What is Aspect-Oriented Programming (AOP)?
 *    AOP complements Object-Oriented Programming (OOP) by providing another way
 *    of thinking about program structure. The key unit of modularity in OOP is
 *    the class, whereas in AOP it is the aspect. AOP isolates cross-cutting
 *    concerns (such as logging, transaction management, security, and metrics)
 *    from core business logic.
 * 
 * 2. Key AOP Concepts:
 *    - Aspect: A module that encapsulates a cross-cutting concern (e.g., this LoggingAspect class).
 *    - Join Point: A candidate point in the execution of the program where an aspect can be plugged in
 *      (in Spring AOP, a join point is always the execution of a method).
 *    - Pointcut: A predicate expression that matches join points (defines WHERE the advice runs).
 *    - Advice: The actual action taken by an aspect at a join point (e.g., @Before, @After, @AfterReturning).
 *    - Target Object: The object being advised by one or more aspects.
 *    - Weaving: The process of linking aspects with target types to create advised proxy objects.
 * 
 * 3. Annotations:
 *    - @Aspect: Declares this class as an Aspect.
 *    - @Component: Registers this aspect as a Spring Bean for auto-detection.
 *    - @Before: Advice that executes before the matched method begins.
 *    - @After: Advice that executes after the matched method finishes (regardless of outcome).
 *    - @AfterReturning: Advice that executes after the method successfully returns a value.
 */
@Aspect
@Component
public class LoggingAspect {

    /**
     * Pointcut targeting all methods within the controller package.
     */
    @Pointcut("execution(* com.kiet.examproject.controller..*.*(..))")
    public void controllerMethods() {
    }

    /**
     * Pointcut targeting all methods within the service package.
     */
    @Pointcut("execution(* com.kiet.examproject.service..*.*(..))")
    public void serviceMethods() {
    }

    /**
     * Combined Pointcut for both Controller and Service layers.
     */
    @Pointcut("controllerMethods() || serviceMethods()")
    public void applicationMethods() {
    }

    /**
     * @Before Advice:
     * Intercepts and logs method execution before the method body runs.
     */
    @Before("applicationMethods()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        System.out.println("[AOP @Before Advice] >>> Entering: " + className + "." + methodName + "()");
        if (args != null && args.length > 0) {
            System.out.println("                     Arguments: " + Arrays.toString(args));
        }
    }

    /**
     * @After Advice:
     * Intercepts and logs method completion (runs like a finally block).
     */
    @After("applicationMethods()")
    public void logAfterMethod(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        System.out.println("[AOP @After Advice]  <<< Finished: " + className + "." + methodName + "()");
    }

    /**
     * @AfterReturning Advice:
     * Intercepts when a method returns normally and logs the returned value.
     */
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("[AOP @AfterReturning] Controller method " + methodName + "() returned: " + result);
    }
}
