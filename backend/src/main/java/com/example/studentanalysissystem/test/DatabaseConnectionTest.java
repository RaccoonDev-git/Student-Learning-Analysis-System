package com.example.studentanalysissystem.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

// @Component  // 注释掉，避免在主应用中自动加载
public class DatabaseConnectionTest implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) {
        System.out.println("测试数据库连接...");
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("✅ 数据库连接成功!");
            System.out.println("数据库URL: " + connection.getMetaData().getURL());
            System.out.println("数据库用户名: " + connection.getMetaData().getUserName());
            System.out.println("数据库产品名称: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("数据库版本: " + connection.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            System.out.println("❌ 数据库连接失败!");
            e.printStackTrace();
        }
    }
}