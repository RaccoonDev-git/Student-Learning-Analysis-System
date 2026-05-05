package com.example.studentanalysissystem.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication(exclude = {
        // Exclude unnecessary auto-configurations
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
})
public class MinimalDbApp {
    
    public static void main(String[] args) {
        SpringApplication.run(MinimalDbApp.class, args);
    }
    
    // @Component  // 注释掉，避免在主应用中自动加载
    public static class DbChecker implements CommandLineRunner {
        
        @Autowired
        private DataSource dataSource;
        
        @Autowired
        private JdbcTemplate jdbcTemplate;
        
        @Override
        public void run(String... args) throws Exception {
            System.out.println("\n=== Minimal Spring Boot Database Connection Test ===");
            
            try {
                // Test data source connection
                Connection connection = dataSource.getConnection();
                System.out.println("[OK] Data source connection successful!");
                System.out.println("Database URL: " + connection.getMetaData().getURL());
                System.out.println("Database username: " + connection.getMetaData().getUserName());
                System.out.println("Database product name: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("Database version: " + connection.getMetaData().getDatabaseProductVersion());
                connection.close();
                
                // Test JdbcTemplate query
                System.out.println("\n[OK] JdbcTemplate query successful, result: " + 
                                   jdbcTemplate.queryForObject("SELECT 1", Integer.class));
                
                // List tables
                System.out.println("\nTables in database:");
                jdbcTemplate.query("SHOW TABLES", (rs) -> {
                    while (rs.next()) {
                        System.out.println("- " + rs.getString(1));
                    }
                    return null;
                });
                
                System.out.println("\n[OK] All tests passed! Database connection is normal.");
                System.exit(0); // Success exit
                
            } catch (SQLException e) {
                System.out.println("[ERROR] Database connection failed!");
                System.out.println("Error message: " + e.getMessage());
                e.printStackTrace();
                System.exit(1); // Failed exit
            }
        }
    }
}