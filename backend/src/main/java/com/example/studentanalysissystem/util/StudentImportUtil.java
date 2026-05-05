package com.example.studentanalysissystem.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生数据导入工具类
 * 支持CSV、Excel、JSON格式
 */
public class StudentImportUtil {

    /**
     * 解析CSV文件
     */
    public static List<Map<String, String>> parseCSV(InputStream inputStream) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String line;
        int lineNumber = 0;
        String[] headers = null;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            // 移除 UTF-8 BOM (EF BB BF)
            if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }

            // 第一行作为表头
            if (lineNumber == 1) {
                headers = line.split(",");
                // 清理表头，移除空格和特殊字符
                for (int i = 0; i < headers.length; i++) {
                    headers[i] = headers[i].trim();
                }
                continue;
            }

            // 跳过空行
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] values = line.split(",", -1); // -1保留空值
            Map<String, String> row = new HashMap<>();

            for (int i = 0; i < headers.length && i < values.length; i++) {
                String key = headers[i];
                String value = values[i].trim();
                row.put(key, value);
            }

            result.add(row);
        }

        reader.close();
        return result;
    }

    /**
     * 解析Excel文件
     */
    public static List<Map<String, String>> parseExcel(InputStream inputStream) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // 读取第一个sheet

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            workbook.close();
            throw new Exception("Excel文件格式错误：找不到表头行");
        }

        // 读取表头
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(getCellValueAsString(cell));
        }

        // 读取数据行
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Map<String, String> rowData = new HashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                String value = cell != null ? getCellValueAsString(cell) : "";
                rowData.put(headers.get(j), value);
            }

            // 跳过空行（所有字段都为空）
            if (rowData.values().stream().allMatch(String::isEmpty)) {
                continue;
            }

            result.add(rowData);
        }

        workbook.close();
        return result;
    }

    /**
     * 解析JSON文件
     */
    public static List<Map<String, String>> parseJSON(InputStream inputStream) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(inputStream);

        if (!rootNode.isArray()) {
            throw new Exception("JSON文件格式错误：根节点必须是数组");
        }

        for (JsonNode node : rootNode) {
            Map<String, String> row = new HashMap<>();
            row.put("姓名", node.has("姓名") ? node.get("姓名").asText() : node.has("name") ? node.get("name").asText() : "");
            row.put("学号", node.has("学号") ? node.get("学号").asText()
                    : node.has("studentNumber") ? node.get("studentNumber").asText() : "");
            row.put("年级", node.has("年级") ? node.get("年级").asText()
                    : node.has("gradeLevel") ? node.get("gradeLevel").asText() : "");
            row.put("班级", node.has("班级") ? node.get("班级").asText()
                    : node.has("className") ? node.get("className").asText() : "");
            row.put("专业",
                    node.has("专业") ? node.get("专业").asText() : node.has("major") ? node.get("major").asText() : "");
            row.put("手机号",
                    node.has("手机号") ? node.get("手机号").asText() : node.has("phone") ? node.get("phone").asText() : "");
            row.put("备注",
                    node.has("备注") ? node.get("备注").asText() : node.has("remarks") ? node.get("remarks").asText() : "");
            result.add(row);
        }

        return result;
    }

    /**
     * 获取Excel单元格的字符串值
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 处理数字，避免科学计数法
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * 根据文件名确定文件类型
     */
    public static String getFileType(String filename) {
        if (filename == null) {
            return "unknown";
        }
        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".csv")) {
            return "csv";
        } else if (lowerName.endsWith(".xlsx") || lowerName.endsWith(".xls")) {
            return "excel";
        } else if (lowerName.endsWith(".json")) {
            return "json";
        } else {
            return "unknown";
        }
    }
}
