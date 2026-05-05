package com.example.studentanalysissystem.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleJdbcTest {

    public static void main(String[] args) {
        // 数据库连接信息，与application.properties中的配置保持一致
        String url = "jdbc:mysql://localhost:3306/student_analysis?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "123456";
        
        System.out.println("使用JDBC直接测试数据库连接...");
        
        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL驱动加载成功!");
            
            // 建立数据库连接
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ 数据库连接成功!");
            System.out.println("数据库URL: " + connection.getMetaData().getURL());
            System.out.println("数据库用户名: " + connection.getMetaData().getUserName());
            System.out.println("数据库产品名称: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("数据库版本: " + connection.getMetaData().getDatabaseProductVersion());
            
            // 关闭连接
            connection.close();
            System.out.println("数据库连接已关闭。");
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL驱动加载失败!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ 数据库连接失败!");
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
        }
    }
}