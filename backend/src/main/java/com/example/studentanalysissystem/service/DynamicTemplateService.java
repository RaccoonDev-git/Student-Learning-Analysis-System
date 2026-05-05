package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.model.Course;
import com.example.studentanalysissystem.model.GradeType;
import com.example.studentanalysissystem.repository.CourseRepository;
import com.example.studentanalysissystem.repository.GradeTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态模板生成服务
 * 根据课程配置生成对应的导入模板
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicTemplateService {

    private final CourseRepository courseRepository;
    private final GradeTypeRepository gradeTypeRepository;

    /**
     * 生成课程专用模板
     */
    public byte[] generateCourseTemplate(Long courseId) throws IOException {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("课程不存在: " + courseId));

        // 获取该课程的平时分类型配置
        List<GradeType> gradeTypes = gradeTypeRepository.findAll();

        // 构建CSV内容
        List<String> headers = buildHeaders(gradeTypes);
        List<List<String>> sampleData = buildSampleData(gradeTypes);

        return generateCSV(headers, sampleData);
    }

    /**
     * 生成通用模板（基于所有平时分类型）
     */
    public byte[] generateGenericTemplate() throws IOException {
        List<GradeType> gradeTypes = gradeTypeRepository.findAll();

        List<String> headers = buildHeaders(gradeTypes);
        List<List<String>> sampleData = buildSampleData(gradeTypes);

        return generateCSV(headers, sampleData);
    }

    /**
     * 根据课程类型生成预设模板
     */
    public byte[] generatePresetTemplate(String courseType) throws IOException {
        List<String> headers = new ArrayList<>();
        List<List<String>> sampleData = new ArrayList<>();

        // 基础字段
        headers.addAll(List.of("姓名", "学号", "年级", "班级", "专业", "课程名称", "学期", "学年"));

        // 根据课程类型添加特定的平时分字段
        switch (courseType.toLowerCase()) {
            case "编译原理":
                headers.addAll(List.of(
                        "编译系统概论作业", "词法分析随堂测", "词法分析",
                        "上下文无关文法随堂测", "自上而下语法分析随堂测", "自上而下语法分析",
                        "自下而上语法分析随堂测", "自下而上语法分析", "语法制导翻译随堂测"));
                break;
            case "数据结构":
                headers.addAll(List.of(
                        "线性表作业", "栈和队列实验", "树和二叉树作业",
                        "图论实验", "排序算法随堂测", "查找算法作业"));
                break;
            case "高等数学":
                headers.addAll(List.of(
                        "第一章作业", "第二章作业", "第三章作业",
                        "期中测验", "第四章作业", "第五章作业", "第六章作业"));
                break;
            case "英语":
                headers.addAll(List.of(
                        "听力练习", "口语测试", "阅读理解",
                        "写作作业", "词汇测试", "语法练习"));
                break;
            default:
                // 通用模板
                headers.addAll(List.of(
                        "平时作业1", "平时作业2", "实验报告1",
                        "实验报告2", "随堂测试1", "随堂测试2"));
        }

        // 添加固定字段
        headers.addAll(List.of("期末考试成绩", "补考成绩", "出勤率", "备注"));

        // 生成示例数据
        sampleData.add(List.of(
                "张三", "20191001", "2019", "19软工A1", "软件工程", courseType, "2024春季", "2023-2024",
                "95", "88", "92", "85", "78", "90", "87", "92", "89", "78", "", "95%", "优秀学生"));

        return generateCSV(headers, sampleData);
    }

    /**
     * 构建表头
     */
    private List<String> buildHeaders(List<GradeType> gradeTypes) {
        List<String> headers = new ArrayList<>();

        // 基础字段
        headers.addAll(List.of("姓名", "学号", "年级", "班级", "专业", "课程名称", "学期", "学年"));

        // 平时分字段（按排序顺序）
        gradeTypes.stream()
                .filter(GradeType::getIsRegular)
                .sorted((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()))
                .forEach(gt -> headers.add(gt.getTypeName()));

        // 固定字段
        headers.addAll(List.of("期末考试成绩", "补考成绩", "出勤率", "备注"));

        return headers;
    }

    /**
     * 构建示例数据
     */
    private List<List<String>> buildSampleData(List<GradeType> gradeTypes) {
        List<List<String>> sampleData = new ArrayList<>();

        // 示例学生数据
        List<String> student1 = new ArrayList<>();
        student1.addAll(List.of("张三", "20191001", "2019", "19软工A1", "软件工程", "编译原理", "2024春季", "2023-2024"));

        // 添加平时分示例数据
        gradeTypes.stream()
                .filter(GradeType::getIsRegular)
                .sorted((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()))
                .forEach(gt -> student1.add("95"));

        // 添加固定字段示例数据
        student1.addAll(List.of("78", "", "95%", "优秀学生"));
        sampleData.add(student1);

        // 第二个示例学生
        List<String> student2 = new ArrayList<>();
        student2.addAll(List.of("李四", "20191002", "2019", "19软工A1", "软件工程", "编译原理", "2024春季", "2023-2024"));

        gradeTypes.stream()
                .filter(GradeType::getIsRegular)
                .sorted((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()))
                .forEach(gt -> student2.add("85"));

        student2.addAll(List.of("65", "75", "85%", "已补考"));
        sampleData.add(student2);

        return sampleData;
    }

    /**
     * 生成CSV文件
     */
    private byte[] generateCSV(List<String> headers, List<List<String>> data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);

        // 写入BOM以支持Excel正确显示中文
        writer.write('\uFEFF');

        // 写入表头
        writer.write(String.join(",", headers));
        writer.write("\n");

        // 写入数据
        for (List<String> row : data) {
            writer.write(String.join(",", row));
            writer.write("\n");
        }

        writer.close();
        return baos.toByteArray();
    }

    /**
     * 获取支持的课程类型列表
     */
    public List<String> getSupportedCourseTypes() {
        return List.of(
                "编译原理", "数据结构", "高等数学", "英语", "大学物理",
                "计算机基础", "线性代数", "概率论", "操作系统", "数据库原理",
                "软件工程", "计算机网络", "人工智能", "机器学习", "通用模板");
    }
}
