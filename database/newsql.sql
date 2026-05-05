-- ================================================
-- 学生学习情况分析系统 - 数据库初始化脚本
-- 版本: 2.0.0
-- 创建日期: 2025-10-12
-- ================================================
-- 说明: 此脚本将创建完整的数据库结构和初始数据
-- 使用方法: mysql -u root -p < init_database.sql
-- ================================================

-- 设置字符集和时区
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 创建数据库
DROP DATABASE IF EXISTS `student_analysis`;
CREATE DATABASE `student_analysis` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE `student_analysis`;

-- ================================================
-- 表结构定义
-- ================================================

-- 用户表
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '加密密码',
  `role` enum('STUDENT','TEACHER','ADMIN') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '手机号码',
  `status` enum('ACTIVE','INACTIVE','LOCKED') CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_login` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='用户表';

-- 教师表
DROP TABLE IF EXISTS `teachers`;
CREATE TABLE `teachers` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '教师姓名',
  `employee_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '工号',
  `department` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '所属部门',
  `title` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '职称',
  `education` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '学历',
  `specialization` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '专业方向',
  `hire_date` date DEFAULT NULL COMMENT '入职日期',
  `avatar_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remarks` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `has_custom_avatar` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `employee_number` (`employee_number`),
  KEY `idx_employee_number` (`employee_number`),
  KEY `idx_department` (`department`),
  KEY `idx_name` (`name`),
  CONSTRAINT `teachers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='教师表';

-- 学生表
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '学生姓名',
  `student_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '学号',
  `class` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '班级',
  `grade_level` int DEFAULT NULL COMMENT '年级',
  `major` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '专业',
  `enrollment_date` date DEFAULT NULL COMMENT '入学日期',
  `graduation_date` date DEFAULT NULL COMMENT '毕业日期',
  `avatar_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remarks` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `has_custom_avatar` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `student_number` (`student_number`),
  KEY `idx_student_number` (`student_number`),
  KEY `idx_class` (`class`),
  KEY `idx_grade_level` (`grade_level`),
  KEY `idx_name` (`name`),
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='学生表';

-- 课程表
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '课程名称',
  `code` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '课程编号',
  `teacher_id` bigint NOT NULL COMMENT '授课教师ID',
  `description` text CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT '课程描述',
  `credits` int NOT NULL,
  `hours` int DEFAULT NULL COMMENT '课时数',
  `semester` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '学期',
  `academic_year` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '学年',
  `capacity` int DEFAULT NULL COMMENT '容量',
  `status` enum('DRAFT','ACTIVE','COMPLETED','CANCELLED') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `max_students` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_code` (`code`),
  KEY `idx_teacher` (`teacher_id`),
  KEY `idx_semester` (`semester`),
  KEY `idx_status` (`status`),
  KEY `idx_name` (`name`),
  CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='课程表';

-- 选课表
DROP TABLE IF EXISTS `course_enrollments`;
CREATE TABLE `course_enrollments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '选课ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `enrollment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课日期',
  `status` enum('ENROLLED','DROPPED','COMPLETED') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_course` (`student_id`,`course_id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_course` (`course_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `course_enrollments_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `course_enrollments_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='选课表';

-- 成绩类型表
DROP TABLE IF EXISTS `grade_types`;
CREATE TABLE `grade_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `default_weight` decimal(5,2) DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `full_score` decimal(5,2) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `is_final` bit(1) NOT NULL,
  `is_makeup` bit(1) NOT NULL,
  `is_regular` bit(1) NOT NULL,
  `sort_order` int DEFAULT NULL,
  `type_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `type_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_atsopn4ox5p2tdvfgl6kra9hm` (`type_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 成绩表
DROP TABLE IF EXISTS `grades`;
CREATE TABLE `grades` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `exam_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '考试类型: 期中, 期末, 作业, 测验',
  `score` decimal(5,2) NOT NULL COMMENT '分数',
  `max_score` decimal(5,2) DEFAULT '100.00' COMMENT '满分',
  `percentage` decimal(5,2) DEFAULT NULL COMMENT '百分比',
  `grade_level` varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '等级: A, B, C, D, F',
  `exam_date` date DEFAULT NULL COMMENT '考试日期',
  `remarks` text CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT '备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `total_score` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_course` (`course_id`),
  KEY `idx_exam_date` (`exam_date`),
  KEY `idx_student_course` (`student_id`,`course_id`),
  KEY `idx_exam_type` (`exam_type`),
  CONSTRAINT `grades_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `grades_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='成绩表';

-- 课程权重配置表
DROP TABLE IF EXISTS `course_weight_configs`;
CREATE TABLE `course_weight_configs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `final_weight` decimal(5,2) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `makeup_weight` decimal(5,2) DEFAULT NULL,
  `regular_weight` decimal(5,2) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `course_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_74ivy0xe20ljt4jrc7xsf4x0` (`course_id`),
  CONSTRAINT `FK23ivc9ojaqq2f2xq1skl24gav` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 综合成绩表
DROP TABLE IF EXISTS `comprehensive_grades`;
CREATE TABLE `comprehensive_grades` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `academic_year` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `comprehensive_score` decimal(5,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `final_grade` decimal(5,2) DEFAULT NULL,
  `final_score` decimal(5,2) DEFAULT NULL,
  `grade_level` varchar(5) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `has_makeup` bit(1) NOT NULL,
  `is_passed` bit(1) NOT NULL,
  `makeup_score` decimal(5,2) DEFAULT NULL,
  `regular_score` decimal(5,2) DEFAULT NULL,
  `remarks` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `semester` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `course_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc0g4t58c51dt2mxdaucfy2494` (`course_id`),
  KEY `FKq61ajcs5dlqkesjgro91lk225` (`student_id`),
  CONSTRAINT `FKc0g4t58c51dt2mxdaucfy2494` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `FKq61ajcs5dlqkesjgro91lk225` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 学习活动表
DROP TABLE IF EXISTS `learning_activities`;
CREATE TABLE `learning_activities` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `activity_type` enum('LOGIN','LOGOUT','VIEW_MATERIAL','DOWNLOAD_MATERIAL','SUBMIT_ASSIGNMENT','VIEW_GRADE','TAKE_EXAM','TAKE_QUIZ','WATCH_VIDEO','POST_MESSAGE','VIEW_ANNOUNCEMENT') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `activity_data` json DEFAULT NULL COMMENT '活动数据',
  `duration` int DEFAULT NULL COMMENT '持续时间(秒)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_course` (`course_id`),
  KEY `idx_type` (`activity_type`),
  KEY `idx_created` (`created_at`),
  CONSTRAINT `learning_activities_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `learning_activities_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='学习活动表';

-- 通知表
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT '内容',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '类型: SYSTEM, GRADE, COURSE, ANNOUNCEMENT',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_type` (`type`),
  KEY `idx_created` (`created_at`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='通知表';

-- 消息表
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text NOT NULL COMMENT '消息内容',
  `message_type` varchar(20) DEFAULT 'text' COMMENT '消息类型: text, image, file',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `read_at` timestamp NULL DEFAULT NULL COMMENT '读取时间',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_sender_receiver` (`sender_id`,`receiver_id`),
  KEY `idx_is_read` (`is_read`),
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='消息表';

-- 教学资源表
DROP TABLE IF EXISTS `resources`;
CREATE TABLE `resources` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '资源名称',
  `original_filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '原始文件名',
  `file_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '文件类型(pdf, doc, ppt, video等)',
  `file_path` varchar(500) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '文件存储路径',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
  `description` text CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT '资源描述',
  `uploader_id` bigint NOT NULL COMMENT '上传者ID',
  `uploader_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '上传者姓名',
  `course_id` bigint DEFAULT NULL COMMENT '关联课程ID',
  `category` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '资源分类(课件、作业、试卷、素材等)',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否有效',
  PRIMARY KEY (`id`),
  KEY `idx_file_type` (`file_type`),
  KEY `idx_uploader` (`uploader_id`),
  KEY `idx_course` (`course_id`),
  KEY `idx_category` (`category`),
  KEY `idx_upload_time` (`upload_time`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='教学资源表';

-- 学生预警表
DROP TABLE IF EXISTS `student_warnings`;
CREATE TABLE `student_warnings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `academic_year` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `current_regular_score` decimal(5,2) DEFAULT NULL,
  `handle_remarks` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `handled_at` datetime(6) DEFAULT NULL,
  `handled_by` bigint DEFAULT NULL,
  `is_handled` bit(1) NOT NULL,
  `semester` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `title` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `warning_level` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `warning_threshold` decimal(5,2) DEFAULT NULL,
  `warning_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `course_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrnk1f0pr0aal07ky6uyjcoh0k` (`course_id`),
  KEY `FKhyn2q1wh3ca3ncbd51y9um9qy` (`student_id`),
  CONSTRAINT `FKhyn2q1wh3ca3ncbd51y9um9qy` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
  CONSTRAINT `FKrnk1f0pr0aal07ky6uyjcoh0k` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ================================================
-- 初始数据
-- ================================================

-- 插入用户数据
LOCK TABLES `users` WRITE;
INSERT INTO `users` VALUES 
(1,'admin','$2a$10$jmPzOs5rn.tD3.pARAng3.smdvynSosTC5JHnSdHhv.XfYRL8dgAG','ADMIN','admin@example.com','13800000000','ACTIVE',NULL,'2025-10-10 06:11:55','2025-10-11 16:55:50'),
(2,'teacher1','$2a$10$jmPzOs5rn.tD3.pARAng3.smdvynSosTC5JHnSdHhv.XfYRL8dgAG','TEACHER','wangna@example.com','13800000001','ACTIVE','2025-10-12 02:39:51','2025-10-10 06:11:55','2025-10-12 02:39:51'),
(3,'teacher2','$2a$10$jmPzOs5rn.tD3.pARAng3.smdvynSosTC5JHnSdHhv.XfYRL8dgAG','TEACHER','li@example.com','13800000002','ACTIVE',NULL,'2025-10-10 06:11:55','2025-10-11 16:55:50'),
(4,'student1','$2a$10$jmPzOs5rn.tD3.pARAng3.smdvynSosTC5JHnSdHhv.XfYRL8dgAG','STUDENT','shengang@example.com','13800138001','ACTIVE','2025-10-12 02:42:35','2025-10-10 06:11:55','2025-10-12 02:42:35'),
(5,'student2','$2a$10$jmPzOs5rn.tD3.pARAng3.smdvynSosTC5JHnSdHhv.XfYRL8dgAG','STUDENT','jiayang@example.com','13800138002','ACTIVE',NULL,'2025-10-10 06:11:55','2025-10-11 16:55:50'),
(6,'student3','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi','STUDENT','wangyicheng@example.com','13800138003','ACTIVE',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55'),
(7,'student4','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi','STUDENT','mengjiahao@example.com','13800138004','ACTIVE',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55'),
(8,'student5','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi','STUDENT','huanglishuan@example.com','13800138005','ACTIVE',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55'),
(9,'student6','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi','STUDENT','zhangming@example.com','13800138006','ACTIVE',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55'),
(10,'student7','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi','STUDENT','lihua@example.com','13800138007','ACTIVE',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55'),
(11,'student8','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi','STUDENT','wangqiang@example.com','13800138008','ACTIVE',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55'),
(12,'student9','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi','STUDENT','liuyang@example.com','13800138009','ACTIVE',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55'),
(13,'student10','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi','STUDENT','chenjing@example.com','13800138010','ACTIVE',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55');
UNLOCK TABLES;

-- 插入教师数据
LOCK TABLES `teachers` WRITE;
INSERT INTO `teachers` VALUES 
(1,1,'Admin','ADMIN001','Computer Science','Professor','PhD','Computer Science','2020-01-01',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0),
(2,2,'Wang Na','T001','Computer Science','Associate Professor','Master','Compiler','2020-01-01',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0),
(3,3,'Li Professor','T002','Computer Science','Professor','PhD','Data Structure','2020-01-01',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0);
UNLOCK TABLES;

-- 插入学生数据
LOCK TABLES `students` WRITE;
INSERT INTO `students` VALUES 
(1,4,'Shen Gang','20181112737','18CS-A1',2018,'Software Engineering','2018-09-01',NULL,NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0),
(2,5,'Jia Yang','20191111504','19CS-A1',2019,'Software Engineering','2019-09-01',NULL,NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0),
(3,6,'Wang Yicheng','20191112403','19CS-A1',2019,'Software Engineering','2019-09-01',NULL,NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0),
(4,7,'Meng Jiahao','20191112404','19CS-A1',2019,'Software Engineering','2019-09-01',NULL,NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0),
(5,8,'Huang Lishuan','20191112405','19CS-A1',2019,'Software Engineering','2019-09-01',NULL,NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0),
(6,9,'Zhang Ming','20191112406','19CS-A1',2019,'Software Engineering','2019-09-01',NULL,NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0),
(7,10,'Li Hua','20191112407','19CS-A1',2019,'Software Engineering','2019-09-01',NULL,NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0),
(8,11,'Wang Qiang','20191112408','19CS-A1',2019,'Software Engineering','2019-09-01',NULL,NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0),
(9,12,'Liu Yang','20191112409','19CS-A1',2019,'Software Engineering','2019-09-01',NULL,NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0),
(10,13,'Chen Jing','20191112410','19CS-A1',2019,'Software Engineering','2019-09-01',NULL,NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL,0);
UNLOCK TABLES;

-- 插入课程数据
LOCK TABLES `courses` WRITE;
INSERT INTO `courses` VALUES 
(1,'Compiler Principles','CS301',2,'Compiler Principles Course',3,48,'2024Spring','2023-2024',50,'ACTIVE','2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(2,'Data Structure','CS201',3,'Data Structure and Algorithms',4,64,'2024Spring','2023-2024',50,'ACTIVE','2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(3,'Computer Basics','CS101',2,'Computer Fundamentals',2,32,'2024Spring','2023-2024',50,'ACTIVE','2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(4,'English','EN101',2,'College English',2,32,'2024Spring','2023-2024',50,'ACTIVE','2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(5,'Operating Systems','CS401',2,'Operating Systems Course',4,NULL,'2024Spring','2023-2024',NULL,'ACTIVE','2025-10-11 03:30:20','2025-10-11 03:30:20',NULL),
(6,'Computer Networks','CS402',2,'Computer Networks Course',3,NULL,'2024Spring','2023-2024',NULL,'ACTIVE','2025-10-11 03:30:20','2025-10-11 03:30:20',NULL),
(7,'Database Systems','CS403',2,'Database Systems Course',4,NULL,'2024Spring','2023-2024',NULL,'ACTIVE','2025-10-11 03:30:20','2025-10-11 03:30:20',NULL),
(8,'Linear Algebra','MATH201',3,'Linear Algebra Course',3,NULL,'2024Spring','2023-2024',NULL,'ACTIVE','2025-10-11 03:30:20','2025-10-11 03:30:20',NULL),
(9,'English Writing','ENG101',3,'English Writing Course',2,NULL,'2024Spring','2023-2024',NULL,'ACTIVE','2025-10-11 03:30:20','2025-10-11 03:30:20',NULL),
(10,'Advanced Physics','PHYS201',3,'Advanced Physics Course',4,NULL,'2024Spring','2023-2024',NULL,'ACTIVE','2025-10-11 03:30:20','2025-10-11 03:30:20',NULL);
UNLOCK TABLES;

-- 插入选课数据
LOCK TABLES `course_enrollments` WRITE;
INSERT INTO `course_enrollments` VALUES 
(1,1,1,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(2,2,1,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(3,3,1,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(4,4,1,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(5,5,1,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(6,6,1,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(7,7,1,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(8,8,1,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(9,9,1,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(10,10,1,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(11,1,2,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(12,2,2,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(13,3,2,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(14,4,2,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(15,5,2,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(16,6,3,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(17,7,3,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(18,8,3,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(19,9,3,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(20,10,3,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(21,1,4,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(22,2,4,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(23,3,4,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(24,4,4,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55'),
(25,5,4,'2025-10-10 06:11:55','ENROLLED','2025-10-10 06:11:55','2025-10-10 06:11:55');
UNLOCK TABLES;

-- 插入成绩类型数据
LOCK TABLES `grade_types` WRITE;
INSERT INTO `grade_types` VALUES 
(1,'2025-10-10 14:11:55.000000',20.00,NULL,100.00,1,0,0,1,1,'ATTENDANCE','Attendance','2025-10-10 14:11:55.000000'),
(2,'2025-10-10 14:11:55.000000',30.00,NULL,100.00,1,0,0,1,2,'HOMEWORK','Homework','2025-10-10 14:11:55.000000'),
(3,'2025-10-10 14:11:55.000000',25.00,NULL,100.00,1,0,0,1,3,'LAB','Lab Report','2025-10-10 14:11:55.000000'),
(4,'2025-10-10 14:11:55.000000',25.00,NULL,100.00,1,0,0,1,4,'QUIZ','Quiz','2025-10-10 14:11:55.000000'),
(5,'2025-10-10 14:11:55.000000',100.00,NULL,100.00,1,1,0,0,5,'FINAL','Final Exam','2025-10-10 14:11:55.000000'),
(6,'2025-10-10 14:11:55.000000',100.00,NULL,100.00,1,0,1,0,6,'MAKEUP','Makeup Exam','2025-10-10 14:11:55.000000');
UNLOCK TABLES;

-- 插入课程权重配置数据
LOCK TABLES `course_weight_configs` WRITE;
INSERT INTO `course_weight_configs` VALUES 
(1,'2025-10-10 14:11:55.000000','Compiler Principles Weight Config',70.00,1,100.00,30.00,'2025-10-11 18:32:48.000000',1),
(2,'2025-10-10 14:11:55.000000','Data Structure Weight Config',70.00,1,100.00,30.00,'2025-10-11 06:08:09.313574',2),
(3,'2025-10-10 14:11:55.000000','Computer Basics Weight Config',30.00,1,70.00,70.00,'2025-10-12 10:40:56.000000',3),
(4,'2025-10-10 14:11:55.000000','English Weight Config',80.00,1,70.00,20.00,'2025-10-10 14:11:55.000000',4);
UNLOCK TABLES;

-- 插入成绩数据（示例数据）
LOCK TABLES `grades` WRITE;
INSERT INTO `grades` VALUES 
(1,1,1,'FINAL',65.00,100.00,65.00,'D','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(2,2,1,'FINAL',78.00,100.00,78.00,'C+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(3,3,1,'FINAL',85.00,100.00,85.00,'B+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(4,4,1,'FINAL',72.00,100.00,72.00,'C','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(5,5,1,'FINAL',88.00,100.00,88.00,'B+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(6,6,1,'FINAL',76.00,100.00,76.00,'C+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(7,7,1,'FINAL',82.00,100.00,82.00,'B-','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(8,8,1,'FINAL',69.00,100.00,69.00,'D+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(9,9,1,'FINAL',91.00,100.00,91.00,'A-','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(10,10,1,'FINAL',74.00,100.00,74.00,'C','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(11,1,2,'FINAL',88.00,100.00,88.00,'B+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(12,2,2,'FINAL',92.00,100.00,92.00,'A-','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(13,3,2,'FINAL',85.00,100.00,85.00,'B+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(14,4,2,'FINAL',78.00,100.00,78.00,'C+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(15,5,2,'FINAL',95.00,100.00,95.00,'A','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(16,6,3,'FINAL',82.00,100.00,82.00,'B-','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(17,7,3,'FINAL',76.00,100.00,76.00,'C+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(18,8,3,'FINAL',89.00,100.00,89.00,'B+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(19,9,3,'FINAL',84.00,100.00,84.00,'B','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(20,10,3,'FINAL',91.00,100.00,91.00,'A-','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(21,1,4,'FINAL',85.00,100.00,85.00,'B+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(22,2,4,'FINAL',78.00,100.00,78.00,'C+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(23,3,4,'FINAL',92.00,100.00,92.00,'A-','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(24,4,4,'FINAL',88.00,100.00,88.00,'B+','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL),
(25,5,4,'FINAL',86.00,100.00,86.00,'B','2024-06-15',NULL,'2025-10-10 06:11:55','2025-10-10 06:11:55',NULL);
UNLOCK TABLES;

-- 插入综合成绩数据（示例数据）
LOCK TABLES `comprehensive_grades` WRITE;
INSERT INTO `comprehensive_grades` VALUES 
(1,'2023-2024',69.50,'2025-10-10 14:11:55.000000',45.50,65.00,'F',0,0,NULL,80.00,NULL,'2024Spring','2025-10-11 15:35:12.254768',1,1),
(2,'2023-2024',79.20,'2025-10-10 14:11:55.000000',54.60,78.00,'F',0,0,NULL,82.00,NULL,'2024Spring','2025-10-11 15:35:12.265766',1,2),
(3,'2023-2024',85.90,'2025-10-10 14:11:55.000000',59.50,85.00,'F',0,0,NULL,88.00,NULL,'2024Spring','2025-10-11 15:35:12.270793',1,3),
(4,'2023-2024',75.90,'2025-10-10 14:11:55.000000',50.40,72.00,'F',0,0,NULL,85.00,NULL,'2024Spring','2025-10-11 15:35:12.278766',1,4),
(5,'2023-2024',61.60,'2025-10-10 14:11:55.000000',61.60,88.00,'D',0,1,NULL,0.00,NULL,'2024Spring','2025-10-11 15:35:12.283767',1,5),
(6,'2023-2024',53.20,'2025-10-10 14:11:55.000000',53.20,76.00,'F',0,0,NULL,0.00,NULL,'2024Spring','2025-10-11 15:35:12.289767',1,6),
(7,'2023-2024',57.40,'2025-10-10 14:11:55.000000',57.40,82.00,'F',0,0,NULL,0.00,NULL,'2024Spring','2025-10-11 15:35:12.296769',1,7),
(8,'2023-2024',48.30,'2025-10-10 14:11:55.000000',48.30,69.00,'F',0,0,NULL,0.00,NULL,'2024Spring','2025-10-11 15:35:12.301766',1,8),
(9,'2023-2024',63.70,'2025-10-10 14:11:55.000000',63.70,91.00,'D',0,1,NULL,0.00,NULL,'2024Spring','2025-10-11 15:35:12.310766',1,9),
(10,'2023-2024',51.80,'2025-10-10 14:11:55.000000',51.80,74.00,'F',0,0,NULL,0.00,NULL,'2024Spring','2025-10-11 15:35:12.314766',1,10),
(11,'2023-2024',84.10,'2025-10-10 14:11:55.000000',61.60,88.00,'D',0,1,NULL,75.00,NULL,'2024Spring','2025-10-11 16:49:00.430639',2,1),
(12,'2023-2024',89.90,'2025-10-10 14:11:55.000000',64.40,92.00,'D',0,1,NULL,85.00,NULL,'2024Spring','2025-10-11 16:49:00.441440',2,2),
(13,'2023-2024',86.50,'2025-10-10 14:11:55.000000',59.50,85.00,'F',0,0,NULL,90.00,NULL,'2024Spring','2025-10-11 16:49:00.448435',2,3),
(14,'2023-2024',78.15,'2025-10-10 14:11:55.000000',54.60,78.00,'F',0,0,NULL,78.50,NULL,'2024Spring','2025-10-11 16:49:00.455773',2,4),
(15,'2023-2024',66.50,'2025-10-10 14:11:55.000000',66.50,95.00,'D',0,1,NULL,0.00,NULL,'2024Spring','2025-10-11 16:49:00.463434',2,5),
(16,'2023-2024',41.00,'2025-10-10 14:11:55.000000',41.00,82.00,'F',0,0,NULL,0.00,NULL,'2024Spring','2025-10-11 16:49:00.480326',3,6),
(17,'2023-2024',38.00,'2025-10-10 14:11:55.000000',38.00,76.00,'F',0,0,NULL,0.00,NULL,'2024Spring','2025-10-11 16:49:00.492320',3,7),
(18,'2023-2024',44.50,'2025-10-10 14:11:55.000000',44.50,89.00,'F',0,0,NULL,0.00,NULL,'2024Spring','2025-10-11 16:49:00.499668',3,8),
(19,'2023-2024',42.00,'2025-10-10 14:11:55.000000',42.00,84.00,'F',0,0,NULL,0.00,NULL,'2024Spring','2025-10-11 16:49:00.515071',3,9),
(20,'2023-2024',45.50,'2025-10-10 14:11:55.000000',45.50,91.00,'F',0,0,NULL,0.00,NULL,'2024Spring','2025-10-11 16:49:00.530068',3,10),
(21,'2023-2024',85.60,'2025-10-10 14:11:55.000000',68.00,85.00,'D',0,1,NULL,88.00,NULL,'2024Spring','2025-10-11 16:49:00.546894',4,1),
(22,'2023-2024',80.40,'2025-10-10 14:11:55.000000',62.40,78.00,'D',0,1,NULL,90.00,NULL,'2024Spring','2025-10-11 16:49:00.558883',4,2),
(23,'2023-2024',92.60,'2025-10-10 14:11:55.000000',73.60,92.00,'C',0,1,NULL,95.00,NULL,'2024Spring','2025-10-11 16:49:00.567887',4,3),
(24,'2023-2024',89.20,'2025-10-10 14:11:55.000000',70.40,88.00,'C',0,1,NULL,92.00,NULL,'2024Spring','2025-10-11 16:49:00.585598',4,4),
(25,'2023-2024',68.80,'2025-10-10 14:11:55.000000',68.80,86.00,'D',0,1,NULL,0.00,NULL,'2024Spring','2025-10-11 16:49:00.599734',4,5);
UNLOCK TABLES;

-- 插入学习活动数据（示例数据）
LOCK TABLES `learning_activities` WRITE;
INSERT INTO `learning_activities` VALUES 
(1,1,1,'SUBMIT_ASSIGNMENT','{\"score\": 65.0, \"max_score\": 100.0, \"assignment_name\": \"Compiler System Introduction\"}',30,'2025-10-10 06:11:55'),
(2,2,1,'SUBMIT_ASSIGNMENT','{\"score\": 78.0, \"max_score\": 100.0, \"assignment_name\": \"Lexical Analysis\"}',45,'2025-10-10 06:11:55'),
(3,3,1,'SUBMIT_ASSIGNMENT','{\"score\": 85.0, \"max_score\": 100.0, \"assignment_name\": \"Syntax Analysis Lab\"}',60,'2025-10-10 06:11:55'),
(4,4,1,'TAKE_QUIZ','{\"score\": 72.0, \"max_score\": 100.0, \"quiz_name\": \"Lexical Analysis Quiz\"}',20,'2025-10-10 06:11:55'),
(5,5,1,'SUBMIT_ASSIGNMENT','{\"score\": 88.0, \"max_score\": 100.0, \"assignment_name\": \"Syntax-Directed Translation\"}',50,'2025-10-10 06:11:55');
UNLOCK TABLES;

-- 插入通知数据（示例数据）
LOCK TABLES `notifications` WRITE;
INSERT INTO `notifications` VALUES 
(1,5,'新成绩发布','您的数据结构期末考试成绩已发布,请查看','GRADE',0,'2025-10-05 02:24:51'),
(2,5,'选课通知','下学期选课系统将于下周一开放','COURSE',0,'2025-10-05 02:24:51'),
(3,5,'系统维护通知','系统将于本周六进行维护,届时无法访问','SYSTEM',1,'2025-10-05 02:24:51'),
(4,6,'作业提醒','数据结构作业2即将截止,请及时提交','COURSE',0,'2025-10-05 02:24:51'),
(5,7,'成绩公布','高等数学期末成绩已公布','GRADE',0,'2025-10-05 02:24:51');
UNLOCK TABLES;

-- 插入消息数据（示例数据）
LOCK TABLES `messages` WRITE;
INSERT INTO `messages` VALUES 
(1,2,1,'Please complete the Compiler System Introduction assignment on time','COURSE',0,'2025-10-10 06:11:55',NULL),
(2,2,8,'Your attendance rate is low, please pay attention to course learning','WARNING',0,'2025-10-10 06:11:55',NULL),
(3,2,10,'Your comprehensive score is close to the pass line, please study harder','GRADE',0,'2025-10-10 06:11:55',NULL);
UNLOCK TABLES;

-- 插入资源数据（示例数据）
LOCK TABLES `resources` WRITE;
INSERT INTO `resources` VALUES 
(1,'Compiler Principles Chapter 1','compiler_chapter1.pdf','PDF','/uploads/compiler_chapter1.pdf',2048000,'Compiler System Introduction Courseware',2,'Wang Na',1,'COURSEWARE',0,'2025-10-10 14:11:55',NULL,1),
(2,'Lexical Analysis Lab Guide','lexical_analysis_lab.pdf','PDF','/uploads/lexical_analysis_lab.pdf',1536000,'Lexical Analysis Lab Guide Book',2,'Wang Na',1,'LAB_GUIDE',0,'2025-10-10 14:11:55',NULL,1),
(3,'Data Structure Algorithm Implementation','data_structure_code.zip','ZIP','/uploads/data_structure_code.zip',5120000,'Data Structure Algorithm Implementation Code',2,'Wang Na',2,'CODE',0,'2025-10-10 14:11:55',NULL,1);
UNLOCK TABLES;

-- 恢复设置
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- ================================================
-- 初始化完成
-- ================================================
-- 所有密码统一为: password123
-- ================================================