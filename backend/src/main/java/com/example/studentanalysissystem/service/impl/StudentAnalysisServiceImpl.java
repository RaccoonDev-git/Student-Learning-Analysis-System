package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.model.*;
import com.example.studentanalysissystem.repository.*;
import com.example.studentanalysissystem.service.StudentAnalysisService;
import com.example.studentanalysissystem.service.AIMiddlewareService;
import com.example.studentanalysissystem.dto.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

/**
 * 学生分析服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentAnalysisServiceImpl implements StudentAnalysisService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final LearningActivityRepository learningActivityRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final AIMiddlewareService aiMiddlewareService;

    @Override
    public StudentAnalysisResponse getStudentComprehensiveAnalysis(Long studentId) {
        log.info("获取学生{}的综合分析数据", studentId);
        
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("学生不存在: " + studentId));
            
            List<Grade> grades = gradeRepository.findByStudentId(studentId);
            List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByStudentId(studentId);
            
            // 构建响应
            return StudentAnalysisResponse.builder()
                    .studentId(student.getId())
                    .studentName(student.getName())
                    .studentNumber(student.getStudentNumber())
                    .className(student.getClassName())
                    .major(student.getMajor())
                    .averageScore(calculateAverageScore(grades))
                    .highestScore(calculateHighestScore(grades))
                    .lowestScore(calculateLowestScore(grades))
                    .totalGrades(grades.size())
                    .totalCourses(enrollments.size())
                    .gradeDistribution(calculateGradeDistribution(grades))
                    .courseComparisons(analyzeCourseComparisons(grades))
                    .scoreTrends(analyzeScoreTrends(grades))
                    .learningBehavior(analyzeLearningBehavior(studentId))
                    .comparison(analyzeComparison(studentId, grades))
                    .learningSuggestions(generateLearningSuggestions(grades))
                    .build();
                    
        } catch (Exception e) {
            log.error("获取学生综合分析失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取学生综合分析失败: " + e.getMessage());
        }
    }

    @Override
    public SubjectAnalysisResponse getSubjectAnalysis(Long studentId, String subject) {
        log.info("获取学生{}的科目{}专项分析", studentId, subject);
        
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("学生不存在: " + studentId));
            
            List<Grade> allGrades = gradeRepository.findByStudentId(studentId);
            List<Grade> subjectGrades = allGrades.stream()
                    .filter(grade -> grade.getCourse().getName().equals(subject))
                    .collect(Collectors.toList());
            
            if (subjectGrades.isEmpty()) {
                throw new RuntimeException("该科目没有成绩数据");
            }
            
            return SubjectAnalysisResponse.builder()
                    .studentId(student.getId())
                    .studentName(student.getName())
                    .subjectName(subject)
                    .subjectCode(subjectGrades.get(0).getCourse().getCode())
                    .scoreStatistics(analyzeSubjectScoreStatistics(subjectGrades))
                    .subjectStrengths(identifySubjectStrengths(allGrades))
                    .subjectWeaknesses(identifySubjectWeaknesses(allGrades))
                    .subjectCorrelations(analyzeSubjectCorrelations(allGrades))
                    .subjectSuggestions(generateSubjectSuggestionsWithAI(student, subject, subjectGrades, allGrades))
                    .studyPlan(createSubjectStudyPlan(subject, subjectGrades))
                    .build();
                    
        } catch (Exception e) {
            log.error("获取科目分析失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取科目分析失败: " + e.getMessage());
        }
    }

    @Override
    public ComparisonAnalysisResponse getComparisonAnalysis(Long studentId, String comparisonType) {
        log.info("获取学生{}的对比分析，类型: {}", studentId, comparisonType);
        
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("学生不存在: " + studentId));
            
            List<Grade> grades = gradeRepository.findByStudentId(studentId);
            List<Student> classStudents = studentRepository.findByClassName(student.getClassName());
            List<Student> majorStudents = studentRepository.findByMajor(student.getMajor());
            
            return ComparisonAnalysisResponse.builder()
                    .studentId(student.getId())
                    .studentName(student.getName())
                    .comparisonType(comparisonType)
                    .classRanking(analyzeClassRanking(student, grades, classStudents))
                    .majorRanking(analyzeMajorRanking(student, grades, majorStudents))
                    .historicalComparison(analyzeHistoricalComparison(grades))
                    .goalAchievement(analyzeComparisonGoalAchievement(studentId))
                    .comparisonSuggestions(generateComparisonSuggestions(student, grades))
                    .build();
                    
        } catch (Exception e) {
            log.error("获取对比分析失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取对比分析失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getLearningGoals(Long studentId) {
        // TODO: 实现学习目标获取
        return new HashMap<>();
    }

    @Override
    public boolean setLearningGoals(Long studentId, Map<String, Object> goals) {
        // TODO: 实现学习目标设置
        return true;
    }


    // 私有辅助方法
    private BigDecimal calculateAverageScore(List<Grade> grades) {
        if (grades.isEmpty()) return BigDecimal.ZERO;
        return grades.stream()
                .map(Grade::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(grades.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateHighestScore(List<Grade> grades) {
        return grades.stream()
                .map(Grade::getScore)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateLowestScore(List<Grade> grades) {
        return grades.stream()
                .map(Grade::getScore)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private Map<String, Integer> calculateGradeDistribution(List<Grade> grades) {
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("优秀(90-100)", 0);
        distribution.put("良好(80-89)", 0);
        distribution.put("中等(70-79)", 0);
        distribution.put("及格(60-69)", 0);
        distribution.put("不及格(<60)", 0);
        
        for (Grade grade : grades) {
            double score = grade.getScore().doubleValue();
            if (score >= 90) distribution.put("优秀(90-100)", distribution.get("优秀(90-100)") + 1);
            else if (score >= 80) distribution.put("良好(80-89)", distribution.get("良好(80-89)") + 1);
            else if (score >= 70) distribution.put("中等(70-79)", distribution.get("中等(70-79)") + 1);
            else if (score >= 60) distribution.put("及格(60-69)", distribution.get("及格(60-69)") + 1);
            else distribution.put("不及格(<60)", distribution.get("不及格(<60)") + 1);
        }
        
        return distribution;
    }

    private List<StudentAnalysisResponse.CourseScoreComparison> analyzeCourseComparisons(List<Grade> grades) {
        Map<String, List<Grade>> gradesByCourse = grades.stream()
                .collect(Collectors.groupingBy(grade -> grade.getCourse().getName()));
        
        return gradesByCourse.entrySet().stream()
                .map(entry -> {
                    String courseName = entry.getKey();
                    List<Grade> courseGrades = entry.getValue();
                    BigDecimal avgScore = calculateAverageScore(courseGrades);
                    BigDecimal maxScore = calculateHighestScore(courseGrades);
                    BigDecimal minScore = calculateLowestScore(courseGrades);
                    
                    String performance = avgScore.compareTo(BigDecimal.valueOf(90)) >= 0 ? "优秀" :
                                       avgScore.compareTo(BigDecimal.valueOf(80)) >= 0 ? "良好" :
                                       avgScore.compareTo(BigDecimal.valueOf(70)) >= 0 ? "中等" :
                                       avgScore.compareTo(BigDecimal.valueOf(60)) >= 0 ? "及格" : "不及格";
                    
                    return StudentAnalysisResponse.CourseScoreComparison.builder()
                            .courseName(courseName)
                            .averageScore(avgScore)
                            .maxScore(maxScore)
                            .minScore(minScore)
                            .gradeCount(courseGrades.size())
                            .performance(performance)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<StudentAnalysisResponse.ScoreTrend> analyzeScoreTrends(List<Grade> grades) {
        // 按时间排序并分析趋势
        List<Grade> sortedGrades = grades.stream()
                .sorted(Comparator.comparing(Grade::getCreatedAt))
                .collect(Collectors.toList());
        
        List<StudentAnalysisResponse.ScoreTrend> trends = new ArrayList<>();
        
        // 简化的趋势分析
        if (sortedGrades.size() >= 2) {
            BigDecimal firstHalf = calculateAverageScore(sortedGrades.subList(0, sortedGrades.size() / 2));
            BigDecimal secondHalf = calculateAverageScore(sortedGrades.subList(sortedGrades.size() / 2, sortedGrades.size()));
            
            String trend = secondHalf.compareTo(firstHalf) > 0 ? "上升" : 
                          secondHalf.compareTo(firstHalf) < 0 ? "下降" : "稳定";
            
            trends.add(StudentAnalysisResponse.ScoreTrend.builder()
                    .timePeriod("整体趋势")
                    .averageScore(calculateAverageScore(sortedGrades))
                    .trend(trend)
                    .build());
        }
        
        return trends;
    }


    private StudentAnalysisResponse.LearningBehaviorSummary analyzeLearningBehavior(Long studentId) {
        List<LearningActivity> activities = learningActivityRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        
        Map<String, Integer> activityBySubject = activities.stream()
                .collect(Collectors.groupingBy(
                        activity -> activity.getCourse().getName(),
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        
        String mostActiveSubject = activityBySubject.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("无");
        
        return StudentAnalysisResponse.LearningBehaviorSummary.builder()
                .totalActivities(activities.size())
                .averageActivityScore(BigDecimal.valueOf(85.0)) // 模拟数据
                .mostActiveSubject(mostActiveSubject)
                .learningPattern("积极型")
                .build();
    }

    private StudentAnalysisResponse.ComparisonSummary analyzeComparison(Long studentId, List<Grade> grades) {
        // 简化的对比分析
        return StudentAnalysisResponse.ComparisonSummary.builder()
                .classRank(5)
                .majorRank(12)
                .classAverage(BigDecimal.valueOf(82.5))
                .majorAverage(BigDecimal.valueOf(85.2))
                .performanceLevel("良好")
                .build();
    }

    private List<String> generateLearningSuggestions(List<Grade> grades) {
        List<String> suggestions = new ArrayList<>();
        
        BigDecimal avgScore = calculateAverageScore(grades);
        if (avgScore.compareTo(BigDecimal.valueOf(90)) >= 0) {
            suggestions.add("成绩优秀，继续保持学习状态");
        } else if (avgScore.compareTo(BigDecimal.valueOf(80)) >= 0) {
            suggestions.add("成绩良好，可以尝试挑战更高目标");
        } else if (avgScore.compareTo(BigDecimal.valueOf(70)) >= 0) {
            suggestions.add("成绩中等，建议加强薄弱科目学习");
        } else {
            suggestions.add("需要加强学习，建议制定详细学习计划");
        }
        
        return suggestions;
    }


    private SubjectAnalysisResponse.SubjectScoreStatistics analyzeSubjectScoreStatistics(List<Grade> grades) {
        return SubjectAnalysisResponse.SubjectScoreStatistics.builder()
                .averageScore(calculateAverageScore(grades))
                .highestScore(calculateHighestScore(grades))
                .lowestScore(calculateLowestScore(grades))
                .totalGrades(grades.size())
                .performanceLevel("良好")
                .gradeTrend("稳定")
                .gradeDistribution(new HashMap<>())
                .build();
    }

    private List<SubjectAnalysisResponse.SubjectStrength> identifySubjectStrengths(List<Grade> allGrades) {
        return new ArrayList<>();
    }

    private List<SubjectAnalysisResponse.SubjectWeakness> identifySubjectWeaknesses(List<Grade> allGrades) {
        return new ArrayList<>();
    }

    private List<SubjectAnalysisResponse.SubjectCorrelation> analyzeSubjectCorrelations(List<Grade> allGrades) {
        return new ArrayList<>();
    }

    /**
     * 使用AI生成科目学习建议
     */
    private List<String> generateSubjectSuggestionsWithAI(Student student, String subject, 
                                                          List<Grade> subjectGrades, List<Grade> allGrades) {
        try {
            // 构建科目成绩摘要
            StringBuilder gradeSummary = new StringBuilder();
            gradeSummary.append(String.format("科目：%s\n", subject));
            
            if (!subjectGrades.isEmpty()) {
                BigDecimal avgScore = calculateAverageScore(subjectGrades);
                BigDecimal highestScore = calculateHighestScore(subjectGrades);
                BigDecimal lowestScore = calculateLowestScore(subjectGrades);
                
                gradeSummary.append(String.format("平均分：%.2f分\n", avgScore));
                gradeSummary.append(String.format("最高分：%.2f分\n", highestScore));
                gradeSummary.append(String.format("最低分：%.2f分\n", lowestScore));
                gradeSummary.append(String.format("成绩次数：%d次\n", subjectGrades.size()));
                
                // 添加成绩趋势
                if (subjectGrades.size() > 1) {
                    SubjectAnalysisResponse.SubjectScoreStatistics statistics = analyzeSubjectScoreStatistics(subjectGrades);
                    if (statistics != null && statistics.getGradeTrend() != null) {
                        gradeSummary.append(String.format("成绩趋势：%s\n", statistics.getGradeTrend()));
                    }
                }
            }
            
            // 分析所有科目，找出薄弱科目
            StringBuilder allSubjectsInfo = new StringBuilder();
            List<String> weakSubjects = new ArrayList<>();
            BigDecimal allAvgScore = BigDecimal.ZERO;
            
            if (!allGrades.isEmpty()) {
                allAvgScore = calculateAverageScore(allGrades);
                gradeSummary.append(String.format("整体平均分：%.2f分\n", allAvgScore));
                
                // 按科目分组计算各科目平均分
                Map<String, List<Grade>> gradesBySubject = allGrades.stream()
                        .collect(Collectors.groupingBy(grade -> grade.getCourse().getName()));
                
                allSubjectsInfo.append("所有科目成绩情况：\n");
                for (Map.Entry<String, List<Grade>> entry : gradesBySubject.entrySet()) {
                    String courseName = entry.getKey();
                    List<Grade> courseGrades = entry.getValue();
                    BigDecimal courseAvgScore = calculateAverageScore(courseGrades);
                    
                    allSubjectsInfo.append(String.format("- %s：平均分%.2f分\n", courseName, courseAvgScore));
                    
                    // 判断是否为薄弱科目（平均分低于整体平均分或低于80分）
                    if (courseAvgScore.compareTo(allAvgScore) < 0 || courseAvgScore.compareTo(BigDecimal.valueOf(80)) < 0) {
                        weakSubjects.add(courseName);
                    }
                }
                
                // 添加当前科目与整体对比
                BigDecimal subjectAvgScore = subjectGrades.isEmpty() ? BigDecimal.ZERO : calculateAverageScore(subjectGrades);
                if (subjectAvgScore.compareTo(allAvgScore) < 0) {
                    gradeSummary.append(String.format("该科目平均分(%.2f)低于整体平均分(%.2f)，需要重点关注\n", 
                            subjectAvgScore, allAvgScore));
                } else if (subjectAvgScore.compareTo(allAvgScore) > 0) {
                    gradeSummary.append(String.format("该科目平均分(%.2f)高于整体平均分(%.2f)，表现良好\n", 
                            subjectAvgScore, allAvgScore));
                }
            }
            
            // 构建AI提示词（将系统提示和用户提示合并）
            String weakSubjectsText = weakSubjects.isEmpty() ? "暂无明显薄弱科目" : String.join("、", weakSubjects);
            
            String userPrompt = String.format("""
                    你是一个专业的教学分析师和学习指导专家。请根据学生的科目成绩数据，提供针对性的学习建议。
                    
                    重要要求：
                    1. 必须明确指出学生的薄弱科目，每个建议段落必须以"你的薄弱科目为[科目名称]，可以..."开头
                    2. 如果只有一个薄弱科目：将所有建议整合为一个段落，可以多行书写，形成一个完整的建议段落
                    3. 如果有多个薄弱科目：为每个薄弱科目提供一个独立的建议段落，不同科目的段落之间用双换行符（\\n\\n）分隔
                    4. 建议要具体可操作，不要泛泛而谈，可以详细说明如何提升
                    5. 针对每个薄弱科目的特点和学生的具体情况
                    6. 建议要实用，能够帮助学生提高成绩
                    7. 不要添加序号、项目符号或其他格式标记，直接返回建议文本
                    
                    输出格式示例（单个薄弱科目）：
                    你的薄弱科目为数学，可以每天花30分钟复习基础概念，重点攻克函数和几何部分。建议多做练习题，特别是代数运算和几何证明题，通过反复练习加深理解。同时可以观看相关教学视频，结合课堂笔记进行系统复习。每周进行一次自我测试，检验学习效果，及时调整学习计划。
                    
                    输出格式示例（多个薄弱科目）：
                    你的薄弱科目为数学，可以每天花30分钟复习基础概念，重点攻克函数和几何部分。建议多做练习题，特别是代数运算和几何证明题，通过反复练习加深理解。
                    
                    你的薄弱科目为英语，可以每周背诵100个单词，多做阅读理解练习。建议每天阅读英文文章，提高语感和词汇量。同时可以练习听力，通过听英语新闻或对话来提升听力水平。
                    
                    ---
                    
                    学生信息：
                    姓名：%s
                    学号：%s
                    
                    当前分析科目：%s
                    
                    成绩数据：
                    %s
                    
                    %s
                    
                    薄弱科目识别：%s
                    
                    请基于以上数据，为该学生提供学习建议。重点关注薄弱科目，并给出具体的改进方法。
                    """, student.getName(), student.getStudentNumber(), subject, gradeSummary.toString(), 
                    allSubjectsInfo.toString(), weakSubjectsText);
            
            // 调用AI服务
            AIResponse aiResponse = aiMiddlewareService.chatWithAI(
                    userPrompt, 
                    "subject-analysis", 
                    student.getId().toString()
            );
            
            if (aiResponse != null && aiResponse.getSuccess() && aiResponse.getContent() != null) {
                // 解析AI返回的建议文本，提取建议列表
                return parseAISuggestions(aiResponse.getContent());
            } else {
                log.warn("AI生成科目建议失败，使用默认建议。错误：{}", 
                        aiResponse != null ? aiResponse.getError() : "AI响应为空");
                return getDefaultSuggestions(subjectGrades);
            }
            
        } catch (Exception e) {
            log.error("使用AI生成科目建议失败，使用默认建议", e);
            return getDefaultSuggestions(subjectGrades);
        }
    }
    
    /**
     * 解析AI返回的建议文本，提取建议段落列表
     * 多个薄弱科目用双换行符分隔，单个薄弱科目为一个段落
     */
    private List<String> parseAISuggestions(String aiContent) {
        List<String> suggestions = new ArrayList<>();
        
        if (aiContent == null || aiContent.trim().isEmpty()) {
            return suggestions;
        }
        
        // 先按双换行符分割，得到不同薄弱科目的段落
        String[] paragraphs = aiContent.split("\\n\\n+");
        
        for (String paragraph : paragraphs) {
            String cleaned = paragraph.trim();
            // 去除单行中的换行符，保留为一个段落
            cleaned = cleaned.replaceAll("\\n", "");
            // 去除多余空格
            cleaned = cleaned.replaceAll("\\s+", " ").trim();
            
            if (!cleaned.isEmpty() && cleaned.length() > 10) {
                suggestions.add(cleaned);
            }
        }
        
        // 如果没有按双换行符分割成功，尝试按单换行分割
        if (suggestions.isEmpty()) {
            // 按行分割，去除空行和格式标记
            String[] lines = aiContent.split("\n");
            Pattern numberPattern = Pattern.compile("^[\\d一二三四五六七八九十、\\.\\-\\s]+");
            Pattern bulletPattern = Pattern.compile("^[•\\-\\*\\s]+");
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                // 移除序号、项目符号等格式标记
                line = numberPattern.matcher(line).replaceFirst("");
                line = bulletPattern.matcher(line).replaceFirst("");
                line = line.trim();
                
                // 移除常见的开头标记
                if (line.startsWith("建议") || line.startsWith("建议：") || line.startsWith("建议：")) {
                    line = line.replaceFirst("建议[：:]?\\s*", "");
                }
                
                if (!line.isEmpty() && line.length() > 5) { // 过滤太短的文本
                    suggestions.add(line);
                }
                
                // 最多返回5条建议
                if (suggestions.size() >= 5) {
                    break;
                }
            }
            
            // 如果没有解析到建议，尝试其他方式
            if (suggestions.isEmpty()) {
                // 尝试按句号、分号分割
                String[] sentences = aiContent.split("[。；;]");
                for (String sentence : sentences) {
                    sentence = sentence.trim();
                    if (sentence.length() > 10 && sentence.length() < 100) {
                        suggestions.add(sentence);
                        if (suggestions.size() >= 5) {
                            break;
                        }
                    }
                }
            }
            
            // 如果还是没有，返回整个内容作为一条建议
            if (suggestions.isEmpty()) {
                String cleaned = aiContent.replaceAll("[\\n\\r]+", " ").trim();
                if (cleaned.length() > 0) {
                    suggestions.add(cleaned);
                }
            }
        }
        
        return suggestions.isEmpty() ? getDefaultSuggestions(new ArrayList<>()) : suggestions;
    }
    
    /**
     * 获取默认建议（AI失败时的备用方案）
     */
    private List<String> getDefaultSuggestions(List<Grade> subjectGrades) {
        if (subjectGrades.isEmpty()) {
            return Arrays.asList("暂无成绩数据，建议开始学习并记录学习进度");
        }
        
        BigDecimal avgScore = calculateAverageScore(subjectGrades);
        List<String> suggestions = new ArrayList<>();
        
        // 判断是否为薄弱科目
        boolean isWeak = avgScore.compareTo(BigDecimal.valueOf(80)) < 0;
        
        if (isWeak) {
            // 获取科目名称（需要从grades中获取）
            String subjectName = subjectGrades.isEmpty() ? "当前科目" : subjectGrades.get(0).getCourse().getName();
            suggestions.add(String.format("你的薄弱科目为%s，平均分为%.1f分，需要重点关注", subjectName, avgScore));
            suggestions.add(String.format("你的薄弱科目为%s，可以每天花30分钟复习基础概念，确保理解透彻", subjectName));
            suggestions.add(String.format("你的薄弱科目为%s，可以针对薄弱环节做专项练习，重点攻克难点部分", subjectName));
            suggestions.add(String.format("你的薄弱科目为%s，可以每周完成一套模拟试题，分析错题并总结解题思路", subjectName));
            suggestions.add(String.format("你的薄弱科目为%s，可以多与同学讨论难点问题，互相学习解题方法", subjectName));
        } else if (avgScore.compareTo(BigDecimal.valueOf(90)) >= 0) {
            suggestions.add("成绩优秀，继续保持学习状态");
            suggestions.add("可以尝试更有挑战性的学习内容");
        } else {
            suggestions.add("成绩良好，继续保持努力");
            suggestions.add("可以进一步巩固知识点，提升整体水平");
        }
        
        return suggestions;
    }

    private SubjectAnalysisResponse.SubjectStudyPlan createSubjectStudyPlan(String subject, List<Grade> subjectGrades) {
        return SubjectAnalysisResponse.SubjectStudyPlan.builder()
                .subjectName(subject)
                .studyTasks(new ArrayList<>())
                .studySchedule("每周3次，每次2小时")
                .resources(Arrays.asList("教材", "在线课程"))
                .expectedOutcome("提高成绩到85分以上")
                .build();
    }

    private ComparisonAnalysisResponse.ClassRanking analyzeClassRanking(Student student, List<Grade> grades, List<Student> classStudents) {
        return ComparisonAnalysisResponse.ClassRanking.builder()
                .currentRank(5)
                .totalStudents(classStudents.size())
                .percentile(BigDecimal.valueOf(75.0))
                .rankingLevel("良好")
                .classAverage(BigDecimal.valueOf(82.5))
                .studentAverage(calculateAverageScore(grades))
                .difference(calculateAverageScore(grades).subtract(BigDecimal.valueOf(82.5)))
                .rankingHistory(new ArrayList<>())
                .build();
    }

    private ComparisonAnalysisResponse.MajorRanking analyzeMajorRanking(Student student, List<Grade> grades, List<Student> majorStudents) {
        return ComparisonAnalysisResponse.MajorRanking.builder()
                .currentRank(12)
                .totalStudents(majorStudents.size())
                .percentile(BigDecimal.valueOf(60.0))
                .rankingLevel("中等")
                .majorAverage(BigDecimal.valueOf(85.2))
                .studentAverage(calculateAverageScore(grades))
                .difference(calculateAverageScore(grades).subtract(BigDecimal.valueOf(85.2)))
                .rankingHistory(new ArrayList<>())
                .build();
    }

    private ComparisonAnalysisResponse.HistoricalComparison analyzeHistoricalComparison(List<Grade> grades) {
        return ComparisonAnalysisResponse.HistoricalComparison.builder()
                .periodComparisons(new ArrayList<>())
                .overallTrend("上升")
                .totalImprovement(BigDecimal.valueOf(5.2))
                .improvementAreas(Arrays.asList("数学", "英语"))
                .declineAreas(new ArrayList<>())
                .build();
    }

    private ComparisonAnalysisResponse.GoalAchievement analyzeComparisonGoalAchievement(Long studentId) {
        return ComparisonAnalysisResponse.GoalAchievement.builder()
                .goals(new ArrayList<>())
                .overallAchievementRate(BigDecimal.valueOf(80.0))
                .achievedGoals(Arrays.asList("完成作业", "参加考试"))
                .pendingGoals(Arrays.asList("提高成绩", "加强学习"))
                .exceededGoals(new ArrayList<>())
                .build();
    }

    private List<String> generateComparisonSuggestions(Student student, List<Grade> grades) {
        return Arrays.asList("继续保持学习状态", "可以挑战更高目标");
    }
}
