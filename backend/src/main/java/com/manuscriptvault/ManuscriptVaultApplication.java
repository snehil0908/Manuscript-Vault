package com.manuscriptvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ManuscriptVault - Main Application Entry Point
 * Digital Manuscript Management System
 */
@SpringBootApplication
public class ManuscriptVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManuscriptVaultApplication.class, args);
        System.out.println("======================================");
        System.out.println("  ManuscriptVault Server is Running!");
        System.out.println("  URL: http://localhost:8080");
        System.out.println("======================================");
    }
}
