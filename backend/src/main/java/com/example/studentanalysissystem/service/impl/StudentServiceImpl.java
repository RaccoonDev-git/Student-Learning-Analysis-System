package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.dto.request.CreateStudentRequest;
import com.example.studentanalysissystem.dto.response.StudentResponse;
import com.example.studentanalysissystem.exception.DuplicateResourceException;
import com.example.studentanalysissystem.exception.ResourceNotFoundException;
import com.example.studentanalysissystem.mapper.StudentMapper;
import com.example.studentanalysissystem.model.Student;
import com.example.studentanalysissystem.model.User;
import com.example.studentanalysissystem.model.Grade;
import com.example.studentanalysissystem.repository.CourseEnrollmentRepository;
import com.example.studentanalysissystem.repository.StudentRepository;
import com.example.studentanalysissystem.repository.UserRepository;
import com.example.studentanalysissystem.repository.GradeRepository;
import com.example.studentanalysissystem.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 学生服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentMapper studentMapper;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;

    @Override
    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        // 检查用户是否存在
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // 检查学号是否已存在
        if (studentRepository.existsByStudentNumber(request.getStudentNumber())) {
            throw new DuplicateResourceException("Student", "studentNumber", request.getStudentNumber());
        }

        // 创建学生实体
        Student student = Student.builder()
                .user(user)
                .studentNumber(request.getStudentNumber())
                .name(request.getName())
                .className(request.getClassName())
                .gradeLevel(request.getGradeLevel())
                .major(request.getMajor())
                .remarks(request.getRemarks())
                .build();

        Student savedStudent = studentRepository.save(student);
        return studentMapper.toResponse(savedStudent);
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        return studentMapper.toResponse(student);
    }

    @Override
    public StudentResponse getStudentByStudentNumber(String studentNumber) {
        Student student = studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "studentNumber", studentNumber));
        return studentMapper.toResponse(student);
    }

    @Override
    public StudentResponse getStudentByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId", userId));
        return studentMapper.toResponse(student);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return studentMapper.toResponseList(students);
    }

    @Override
    public List<StudentResponse> getStudentsByClassName(String className) {
        List<Student> students = studentRepository.findByClassName(className);
        return studentMapper.toResponseList(students);
    }

    @Override
    public List<StudentResponse> getStudentsByGradeLevel(Integer gradeLevel) {
        List<Student> students = studentRepository.findByGradeLevel(gradeLevel);
        return studentMapper.toResponseList(students);
    }

    @Override
    public List<StudentResponse> getStudentsByMajor(String major) {
        List<Student> students = studentRepository.findByMajor(major);
        return studentMapper.toResponseList(students);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id,
            com.example.studentanalysissystem.dto.request.UpdateStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        // 更新学生信息(只更新非空字段)
        if (request.getName() != null) {
            student.setName(request.getName());
        }
        if (request.getClassName() != null) {
            student.setClassName(request.getClassName());
        }
        if (request.getGradeLevel() != null) {
            student.setGradeLevel(request.getGradeLevel());
        }
        if (request.getMajor() != null) {
            student.setMajor(request.getMajor());
        }
        if (request.getEnrollmentDate() != null) {
            student.setEnrollmentDate(request.getEnrollmentDate());
        }
        if (request.getGraduationDate() != null) {
            student.setGraduationDate(request.getGraduationDate());
        }
        if (request.getRemarks() != null) {
            student.setRemarks(request.getRemarks());
        }
        if (request.getPhone() != null) {
            // 更新用户表的手机号
            User user = student.getUser();
            user.setPhone(request.getPhone());
            userRepository.save(user);
        }

        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toResponse(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        studentRepository.delete(student);
    }

    @Override
    @Transactional
    public void batchDeleteStudents(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 批量删除学生
        studentRepository.deleteAllById(ids);
    }

    @Override
    public List<StudentResponse> searchStudents(String keyword) {
        List<Student> students = studentRepository.searchByKeyword(keyword);
        return studentMapper.toResponseList(students);
    }

    @Override
    public List<StudentResponse> filterStudents(Integer gradeLevel, String className, String major, String keyword) {
        List<Student> students;

        // 如果所有参数都为空，返回所有学生
        if (gradeLevel == null && className == null && major == null && keyword == null) {
            students = studentRepository.findAll();
        } else {
            // 使用仓库方法进行高级筛选
            students = studentRepository.filterStudents(gradeLevel, className, major, keyword);
        }

        return studentMapper.toResponseList(students);
    }

    @Override
    public Student getStudentEntityById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("学生不存在，ID: " + id));
    }

    @Override
    public List<String> getClassesByCourseId(Long courseId) {
        // 通过选课记录获取选修该课程的学生ID
        List<Long> studentIds = enrollmentRepository.findStudentIdsByCourseId(courseId);
        
        // 获取这些学生的班级信息
        List<Student> students = studentRepository.findAllById(studentIds);
        
        // 提取班级名称并去重
        return students.stream()
                .map(Student::getClassName)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getStudentScoresByCourseAndClass(Long courseId, String className) {
        log.info("获取课程 {} 班级 {} 的学生成绩数据", courseId, className);
        
        // 获取该班级选修该课程的学生
        List<Long> studentIds = enrollmentRepository.findStudentIdsByCourseId(courseId);
        if (studentIds.isEmpty()) {
            log.warn("课程 {} 没有学生选修", courseId);
            return new ArrayList<>();
        }
        
        List<Student> students = studentRepository.findAllById(studentIds);
        
        // 过滤出指定班级的学生
        List<Student> classStudents = students.stream()
                .filter(student -> className.equals(student.getClassName()))
                .collect(Collectors.toList());
        
        if (classStudents.isEmpty()) {
            log.warn("班级 {} 没有学生选修课程 {}", className, courseId);
            return new ArrayList<>();
        }
        
        // 构建学生成绩数据
        return classStudents.stream().map(student -> {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("name", student.getName());
            studentData.put("id", student.getId());
            studentData.put("studentNumber", student.getStudentNumber());
            
            // 从Grade表查询实际成绩
            try {
                List<Grade> gradeList = gradeRepository.findByStudentIdAndCourseId(student.getId(), courseId);
                if (!gradeList.isEmpty()) {
                    double averageScore = gradeList.stream()
                            .mapToDouble(g -> g.getScore().doubleValue())
                            .average()
                            .orElse(0.0);
                    studentData.put("score", averageScore);
                } else {
                    studentData.put("score", 0.0);
                }
            } catch (Exception e) {
                log.error("查询学生 {} 课程 {} 成绩失败", student.getId(), courseId, e);
                studentData.put("score", 0.0);
            }
            
            return studentData;
        }).collect(Collectors.toList());
    }
}
