package com.example.studentanalysissystem.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.studentanalysissystem.test"})
public class DatabaseHealthCheck {
    
    public static void main(String[] args) {
        SpringApplication.run(DatabaseHealthCheck.class, args);
    }
    
    // @Component  // 注释掉，避免在主应用中自动加载
    public static class DatabaseChecker implements CommandLineRunner {
        
        @Autowired
        private DataSource dataSource;
        
        @Autowired
        private JdbcTemplate jdbcTemplate;
        
        @Override
        public void run(String... args) throws Exception {
            System.out.println("\n===== 数据库连接健康检查 =====");
            
            try {
                // 测试基本连接
                Connection connection = dataSource.getConnection();
                System.out.println("✅ 数据源连接成功!");
                System.out.println("数据库URL: " + connection.getMetaData().getURL());
                System.out.println("数据库用户名: " + connection.getMetaData().getUserName());
                System.out.println("数据库产品名称: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("数据库版本: " + connection.getMetaData().getDatabaseProductVersion());
                connection.close();
                
                // 测试JdbcTemplate查询
                System.out.println("\n测试JdbcTemplate查询:");
                Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                System.out.println("✅ JdbcTemplate查询成功，结果: " + result);
                
                // 检查student_analysis数据库中的表
                System.out.println("\n检查student_analysis数据库中的表:");
                jdbcTemplate.query("SHOW TABLES", (rs) -> {
                    while (rs.next()) {
                        System.out.println("- " + rs.getString(1));
                    }
                    return null;
                });
                
            } catch (SQLException e) {
                System.out.println("❌ 数据库连接失败!");
                System.out.println("错误信息: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println("===== 健康检查完成 =====\n");
            // 程序退出
            System.exit(0);
        }
    }
}