-- MySQL数据库初始化脚本

-- 设置客户端连接字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 删除表（如果存在）
DROP TABLE IF EXISTS `englishwords`.`word_submission`;
DROP TABLE IF EXISTS `englishwords`.`user_word_error`;
DROP TABLE IF EXISTS `englishwords`.`exam_record`;
DROP TABLE IF EXISTS `englishwords`.`word`;
DROP TABLE IF EXISTS `englishwords`.`user`;

-- 重新启用外键约束检查
SET FOREIGN_KEY_CHECKS = 1;

-- 创建用户表
CREATE TABLE `englishwords`.`user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(255) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `nick_name` VARCHAR(255) DEFAULT NULL COMMENT '昵称',
  `total_score` INT DEFAULT 0 COMMENT '总积分',
  `role` VARCHAR(50) DEFAULT 'USER' COMMENT '用户角色',
  `grade` INT DEFAULT NULL COMMENT '年级',
  `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_USERNAME` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建单词表
CREATE TABLE `englishwords`.`word` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '单词ID',
  `english` VARCHAR(255) NOT NULL COMMENT '英文单词',
  `chinese` VARCHAR(255) NOT NULL COMMENT '中文翻译',
  `grade` INT NOT NULL COMMENT '年级',
  `unit` INT NOT NULL COMMENT '单元',
  `teacher_id` BIGINT DEFAULT NULL COMMENT '老师ID(单词录入的老师)',
  `teacher_name` VARCHAR(100) DEFAULT NULL COMMENT '老师名字',
  `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_grade_unit` (`grade`, `unit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单词表';

-- 创建考试记录表
CREATE TABLE `englishwords`.`exam_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '考试记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `score` INT NOT NULL COMMENT '考试得分',
  `total_questions` INT NOT NULL COMMENT '总题数',
  `correct_answers` INT NOT NULL COMMENT '正确答案数',
  `wrong_answers` INT NOT NULL COMMENT '错误答案数',
  `exam_time_seconds` INT NOT NULL COMMENT '考试用时（秒）',
  `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_EXAM_RECORD_USER` FOREIGN KEY (`user_id`) REFERENCES `englishwords`.`user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试记录表';

-- 创建用户单词错误记录表
CREATE TABLE `englishwords`.`user_word_error` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `word_id` BIGINT NOT NULL COMMENT '单词ID',
  `error_count` INT NOT NULL DEFAULT 0 COMMENT '错误次数',
  `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_USER_WORD_ERROR_USER` FOREIGN KEY (`user_id`) REFERENCES `englishwords`.`user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_WORD_ERROR_WORD` FOREIGN KEY (`word_id`) REFERENCES `englishwords`.`word` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户单词错误记录表';

-- 创建单词反馈表
CREATE TABLE `englishwords`.`word_feedback` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `word_id` BIGINT NOT NULL COMMENT '单词ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `username` VARCHAR(100) DEFAULT NULL COMMENT '用户名',
  `original_english` VARCHAR(200) DEFAULT NULL COMMENT '原单词英文',
  `original_chinese` VARCHAR(200) DEFAULT NULL COMMENT '原单词中文',
  `feedback_content` TEXT COMMENT '反馈内容',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '反馈状态：PENDING-待处理, RESOLVED-已解决, REJECTED-已拒绝',
  `teacher_id` BIGINT DEFAULT NULL COMMENT '老师ID(单词录入的老师)',
  `teacher_name` VARCHAR(100) DEFAULT NULL COMMENT '老师名字',
  `grade` INT DEFAULT NULL COMMENT '单词年级',
  `unit` INT DEFAULT NULL COMMENT '单词单元',
  `admin_remark` TEXT COMMENT '管理员处理备注',
  `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
  `resolved_at` DATETIME DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`),
  INDEX `idx_word_feedback_status` (`status`),
  INDEX `idx_word_feedback_user` (`user_id`),
  INDEX `idx_word_feedback_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单词反馈表';

-- 创建单词提交审批表
CREATE TABLE `englishwords`.`word_submission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `english` VARCHAR(255) NOT NULL COMMENT '英文单词',
  `chinese` VARCHAR(255) NOT NULL COMMENT '中文翻译',
  `grade` INT NOT NULL COMMENT '年级',
  `unit` INT NOT NULL COMMENT '单元',
  `teacher_id` BIGINT NOT NULL COMMENT '老师ID',
  `teacher_name` VARCHAR(100) DEFAULT NULL COMMENT '老师姓名',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
  `admin_remark` TEXT COMMENT '管理员审批意见',
  `submitted_at` DATETIME DEFAULT NULL COMMENT '提交时间',
  `reviewed_at` DATETIME DEFAULT NULL COMMENT '审批时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单词提交审批表';