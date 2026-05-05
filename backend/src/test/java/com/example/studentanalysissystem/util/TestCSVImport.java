package com.example.studentanalysissystem.util;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * 测试 StudentImportUtil 的CSV解析功能
 */
public class TestCSVImport {
    public static void main(String[] args) {
        try {
            String csvFile = "C:\\Users\\72404\\Downloads\\218273479编译原理（软件工程、计算机科学与技术）_50556873_19软工选课_学生导入格式.csv";

            System.out.println("正在测试CSV文件解析...");
            System.out.println("文件路径: " + csvFile);
            System.out.println();

            FileInputStream fis = new FileInputStream(csvFile);
            List<Map<String, String>> students = StudentImportUtil.parseCSV(fis);

            System.out.println("✓ 解析成功！");
            System.out.println("总记录数: " + students.size());
            System.out.println();

            // 打印前3条记录
            System.out.println("前3条记录:");
            for (int i = 0; i < Math.min(3, students.size()); i++) {
                Map<String, String> student = students.get(i);
                System.out.println("\n记录 " + (i + 1) + ":");
                System.out.println("  字段数量: " + student.size());
                student.forEach((key, value) -> {
                    System.out.println("  [" + key + "] = [" + value + "]");
                });
            }

            // 检查第一条记录的姓名和学号
            if (!students.isEmpty()) {
                Map<String, String> first = students.get(0);
                String name = first.get("姓名");
                String studentNumber = first.get("学号");

                System.out.println("\n验证第一条记录:");
                System.out.println("  姓名为空: " + (name == null || name.isEmpty()));
                System.out.println("  学号为空: " + (studentNumber == null || studentNumber.isEmpty()));

                if (name != null && !name.isEmpty() && studentNumber != null && !studentNumber.isEmpty()) {
                    System.out.println("\n✓ 数据验证通过！");
                } else {
                    System.out.println("\n✗ 数据验证失败！姓名或学号为空");
                }
            }

        } catch (Exception e) {
            System.err.println("✗ 解析失败:");
            e.printStackTrace();
        }
    }
}
