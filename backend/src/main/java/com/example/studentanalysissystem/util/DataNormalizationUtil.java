package com.example.studentanalysissystem.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据格式自动转换工具类
 * 用于在导入时自动清理和标准化各种不规范的数据格式
 */
@Slf4j
@Component
public class DataNormalizationUtil {

    // 手机号正则（支持各种格式）
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:(?:\\+|00)86)?\\s*[1][3-9]\\d{9}");

    // 学号正则（提取数字部分）
    private static final Pattern STUDENT_NUMBER_PATTERN = Pattern.compile("\\d+");

    // 分数正则（支持小数、百分比、分数形式）
    private static final Pattern SCORE_PATTERN = Pattern.compile(
            "(\\d+(?:\\.\\d+)?)\\s*(?:%|分|points?)?");

    // 分数形式（如 85/100）
    private static final Pattern FRACTION_PATTERN = Pattern.compile(
            "(\\d+(?:\\.\\d+)?)\\s*/\\s*(\\d+(?:\\.\\d+)?)");

    /**
     * 标准化整行数据
     */
    public Map<String, String> normalizeRowData(Map<String, String> rowData) {
        Map<String, String> normalized = new HashMap<>();

        for (Map.Entry<String, String> entry : rowData.entrySet()) {
            String key = normalizeKey(entry.getKey());
            String value = normalizeValue(key, entry.getValue());

            if (key != null && value != null && !value.isEmpty()) {
                normalized.put(key, value);
            }
        }

        return normalized;
    }

    /**
     * 标准化字段名（列名）
     */
    private String normalizeKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }

        // 移除空白字符
        String normalized = key.trim();

        // 移除特殊字符（保留中文、英文、数字）
        normalized = normalized.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", "");

        // 统一常见字段名的变体
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("姓名", "姓名");
        fieldMapping.put("名字", "姓名");
        fieldMapping.put("学生姓名", "姓名");
        fieldMapping.put("name", "姓名");

        fieldMapping.put("学号", "学号");
        fieldMapping.put("学生学号", "学号");
        fieldMapping.put("studentnumber", "学号");
        fieldMapping.put("studentid", "学号");
        fieldMapping.put("学生编号", "学号");
        fieldMapping.put("编号", "学号");

        fieldMapping.put("年级", "年级");
        fieldMapping.put("入学年份", "年级");
        fieldMapping.put("grade", "年级");

        fieldMapping.put("班级", "班级");
        fieldMapping.put("class", "班级");
        fieldMapping.put("班", "班级");

        fieldMapping.put("专业", "专业");
        fieldMapping.put("major", "专业");
        fieldMapping.put("所属专业", "专业");

        fieldMapping.put("手机号", "手机号");
        fieldMapping.put("联系方式", "手机号");
        fieldMapping.put("电话", "手机号");
        fieldMapping.put("手机", "手机号");
        fieldMapping.put("phone", "手机号");
        fieldMapping.put("mobile", "手机号");
        fieldMapping.put("联系电话", "手机号");

        fieldMapping.put("备注", "备注");
        fieldMapping.put("说明", "备注");
        fieldMapping.put("note", "备注");
        fieldMapping.put("remark", "备注");

        // 转换为小写进行匹配
        String lowerKey = normalized.toLowerCase();
        for (Map.Entry<String, String> mapping : fieldMapping.entrySet()) {
            if (lowerKey.equals(mapping.getKey().toLowerCase())) {
                return mapping.getValue();
            }
        }

        return normalized;
    }

    /**
     * 标准化字段值
     */
    private String normalizeValue(String key, String value) {
        if (value == null) {
            return null;
        }

        // 移除首尾空白
        String normalized = value.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        // 根据字段类型进行特定处理
        switch (key) {
            case "姓名":
                return normalizeName(normalized);
            case "学号":
                return normalizeStudentNumber(normalized);
            case "年级":
                return normalizeGrade(normalized);
            case "班级":
                return normalizeClass(normalized);
            case "手机号":
                return normalizePhone(normalized);
            default:
                // 如果是课程成绩，进行分数标准化
                if (isScoreField(key)) {
                    return normalizeScore(normalized);
                }
                return normalized;
        }
    }

    /**
     * 标准化姓名
     */
    private String normalizeName(String name) {
        // 移除空白字符
        name = name.replaceAll("\\s+", "");

        // 只保留中文和英文字母
        name = name.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z]", "");

        // 姓名长度验证（2-50个字符）
        if (name.length() < 2 || name.length() > 50) {
            log.warn("姓名长度不合法: {}", name);
            return null;
        }

        return name;
    }

    /**
     * 标准化学号
     */
    private String normalizeStudentNumber(String studentNumber) {
        // 提取数字部分
        Matcher matcher = STUDENT_NUMBER_PATTERN.matcher(studentNumber);
        if (matcher.find()) {
            String number = matcher.group();

            // 学号长度验证（4-20位）
            if (number.length() >= 4 && number.length() <= 20) {
                return number;
            }
        }

        log.warn("学号格式不合法: {}", studentNumber);
        return null;
    }

    /**
     * 标准化年级
     */
    private String normalizeGrade(String grade) {
        // 提取4位数字年份
        Matcher matcher = Pattern.compile("(19|20)\\d{2}").matcher(grade);
        if (matcher.find()) {
            return matcher.group();
        }

        // 如果只是数字，尝试补全为4位年份
        if (grade.matches("\\d{2}")) {
            int year = Integer.parseInt(grade);
            if (year >= 0 && year <= 30) {
                return "20" + grade;
            } else if (year >= 90 && year <= 99) {
                return "19" + grade;
            }
        }

        return grade;
    }

    /**
     * 标准化班级
     */
    private String normalizeClass(String className) {
        // 移除多余空格
        className = className.replaceAll("\\s+", "");

        // 统一常见格式
        // 例如: "软工1班" -> "软工1", "19软工A1班" -> "19软工A1"
        className = className.replaceAll("班$", "");

        return className;
    }

    /**
     * 标准化手机号
     */
    private String normalizePhone(String phone) {
        // 移除所有非数字字符
        String digits = phone.replaceAll("\\D", "");

        // 提取11位手机号
        Matcher matcher = PHONE_PATTERN.matcher(phone);
        if (matcher.find()) {
            String foundPhone = matcher.group().replaceAll("\\D", "");

            // 如果有国家代码，移除
            if (foundPhone.length() == 13 && foundPhone.startsWith("86")) {
                foundPhone = foundPhone.substring(2);
            }

            // 验证是否为11位且以1开头
            if (foundPhone.length() == 11 && foundPhone.startsWith("1")) {
                return foundPhone;
            }
        }

        // 如果是11位数字且以1开头，直接返回
        if (digits.length() == 11 && digits.startsWith("1")) {
            return digits;
        }

        log.warn("手机号格式不合法: {}", phone);
        return null;
    }

    /**
     * 标准化分数
     * 支持多种格式：
     * - 纯数字: "85" -> "85"
     * - 小数: "85.5" -> "85.5"
     * - 百分比: "85%" -> "85"
     * - 带单位: "85分" -> "85"
     * - 分数形式: "85/100" -> "85"
     * - 分数形式: "42.5/50" -> "85" (自动转换为百分制)
     */
    private String normalizeScore(String score) {
        // 移除空白字符
        score = score.trim();

        // 处理空值
        if (score.isEmpty() || score.equals("-") || score.equalsIgnoreCase("null") ||
                score.equalsIgnoreCase("缺考") || score.equalsIgnoreCase("absent")) {
            return null;
        }

        try {
            // 1. 处理分数形式 (如 85/100, 42.5/50)
            Matcher fractionMatcher = FRACTION_PATTERN.matcher(score);
            if (fractionMatcher.find()) {
                double numerator = Double.parseDouble(fractionMatcher.group(1));
                double denominator = Double.parseDouble(fractionMatcher.group(2));

                if (denominator == 0) {
                    log.warn("分数分母为0: {}", score);
                    return null;
                }

                // 转换为百分制
                double percentage = (numerator / denominator) * 100;

                // 四舍五入到一位小数
                BigDecimal result = BigDecimal.valueOf(percentage)
                        .setScale(1, RoundingMode.HALF_UP);

                return result.toString();
            }

            // 2. 处理普通格式（数字、百分比、带单位）
            Matcher scoreMatcher = SCORE_PATTERN.matcher(score);
            if (scoreMatcher.find()) {
                String scoreStr = scoreMatcher.group(1);
                BigDecimal scoreValue = new BigDecimal(scoreStr);

                // 如果是百分比格式，已经是百分制
                if (score.contains("%")) {
                    // 百分比通常是0-100
                    if (scoreValue.compareTo(BigDecimal.ZERO) < 0 ||
                            scoreValue.compareTo(BigDecimal.valueOf(100)) > 0) {
                        log.warn("百分比分数超出范围: {}", score);
                        return null;
                    }
                } else {
                    // 验证分数范围 (0-150)
                    if (scoreValue.compareTo(BigDecimal.ZERO) < 0 ||
                            scoreValue.compareTo(BigDecimal.valueOf(150)) > 0) {
                        log.warn("分数超出范围: {}", score);
                        return null;
                    }
                }

                return scoreValue.toString();
            }

            log.warn("无法解析的分数格式: {}", score);
            return null;

        } catch (NumberFormatException e) {
            log.warn("分数格式错误: {}", score);
            return null;
        }
    }

    /**
     * 判断是否为成绩字段
     */
    private boolean isScoreField(String key) {
        // 排除基本信息字段
        Set<String> basicFields = Set.of("姓名", "学号", "年级", "班级", "专业", "手机号", "备注");
        if (basicFields.contains(key)) {
            return false;
        }

        // 如果字段名包含"课程"、"成绩"等关键字，不是成绩字段本身
        if (key.matches(".*课程\\d+.*") || key.matches(".*成绩\\d+.*")) {
            return false;
        }

        // 其他字段可能是课程名称（成绩字段）
        return true;
    }

    /**
     * 验证标准化后的数据是否完整
     */
    public boolean validateNormalizedData(Map<String, String> data) {
        // 必填字段检查
        if (!data.containsKey("姓名") || data.get("姓名") == null || data.get("姓名").isEmpty()) {
            log.warn("缺少必填字段: 姓名");
            return false;
        }

        if (!data.containsKey("学号") || data.get("学号") == null || data.get("学号").isEmpty()) {
            log.warn("缺少必填字段: 学号");
            return false;
        }

        return true;
    }

    /**
     * 获取数据转换统计信息
     */
    public Map<String, Object> getNormalizationStats(
            Map<String, String> original,
            Map<String, String> normalized) {

        Map<String, Object> stats = new HashMap<>();
        stats.put("originalFields", original.size());
        stats.put("normalizedFields", normalized.size());
        stats.put("droppedFields", original.size() - normalized.size());

        // 统计被修正的字段
        int correctedCount = 0;
        for (String key : original.keySet()) {
            String originalValue = original.get(key);
            String normalizedKey = normalizeKey(key);
            if (normalizedKey != null && normalized.containsKey(normalizedKey)) {
                String normalizedValue = normalized.get(normalizedKey);
                if (!originalValue.equals(normalizedValue)) {
                    correctedCount++;
                }
            }
        }
        stats.put("correctedFields", correctedCount);

        return stats;
    }
}
