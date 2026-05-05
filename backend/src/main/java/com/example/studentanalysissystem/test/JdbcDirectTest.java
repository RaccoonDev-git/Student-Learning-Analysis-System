package com.example.studentanalysissystem.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcDirectTest {

    public static void main(String[] args) {
        // 从application-mysql.properties中复制的数据库连接信息
        String url = "jdbc:mysql://localhost:3306/student_analysis?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "123456";
        
        System.out.println("=== 直接JDBC连接测试 ===");
        System.out.println("连接URL: " + url);
        System.out.println("用户名: " + username);
        
        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL驱动加载成功!");
            
            // 建立数据库连接
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ 数据库连接成功!");
            
            // 显示数据库信息
            System.out.println("数据库产品名称: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("数据库版本: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("数据库URL: " + connection.getMetaData().getURL());
            System.out.println("数据库用户名: " + connection.getMetaData().getUserName());
            
            // 测试简单查询
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("SELECT 1");
            if (resultSet.next()) {
                System.out.println("✅ 查询测试成功，结果: " + resultSet.getInt(1));
            }
            
            // 检查数据库中的表
            System.out.println("\n检查student_analysis数据库中的表:");
            resultSet = statement.executeQuery("SHOW TABLES");
            while (resultSet.next()) {
                System.out.println("- " + resultSet.getString(1));
            }
            
            // 关闭资源
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("\n✅ 测试完成，所有资源已关闭。");
            
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