package com.ott.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OTT Streaming Platform – Backend Management System
 * Team J11 | UE23CS352B – Object Oriented Analysis & Design
 *
 * Architecture : MVC (Spring MVC)
 * Design Patterns applied:
 *   1. Factory Method  (Creational)  – UserFactory
 *   2. Facade          (Structural)  – ContentManagementFacade
 *   3. Strategy        (Behavioral)  – RecommendationStrategy, PaymentService
 *   4. Singleton       (Framework)   – All Spring @Service / @Component beans
 *
 * SOLID Principles: SRP, OCP, LSP, ISP, DIP (see service/pattern layers)
 */
@SpringBootApplication
public class OttPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(OttPlatformApplication.class, args);
    }
}
