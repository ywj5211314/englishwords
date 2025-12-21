-- 创建并初始化英语单词考试系统数据库
-- 注意：请确保MySQL服务正在运行

-- 创建数据库
CREATE DATABASE IF NOT EXISTS englishwords CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE englishwords;

-- 删除现有表（如果存在）
DROP TABLE IF EXISTS user_word_error;
DROP TABLE IF EXISTS exam_record;
DROP TABLE IF EXISTS word;
DROP TABLE IF EXISTS user;

-- 创建用户表
CREATE TABLE user (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(255) NOT NULL COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  nick_name VARCHAR(255) DEFAULT NULL COMMENT '昵称',
  total_score INT DEFAULT 0 COMMENT '总积分',
  role VARCHAR(50) DEFAULT 'USER' COMMENT '用户角色',
  grade INT DEFAULT NULL COMMENT '年级',
  created_at DATETIME DEFAULT NULL COMMENT '创建时间',
  updated_at DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY UK_USERNAME (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建单词表
CREATE TABLE word (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '单词ID',
  english VARCHAR(255) NOT NULL COMMENT '英文单词',
  chinese VARCHAR(255) NOT NULL COMMENT '中文翻译',
  grade INT NOT NULL COMMENT '年级',
  unit INT NOT NULL COMMENT '单元',
  created_at DATETIME DEFAULT NULL COMMENT '创建时间',
  updated_at DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_grade_unit (grade, unit)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单词表';

-- 创建考试记录表
CREATE TABLE exam_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '考试记录ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  score INT NOT NULL COMMENT '考试得分',
  total_questions INT NOT NULL COMMENT '总题数',
  correct_answers INT NOT NULL COMMENT '正确答案数',
  wrong_answers INT NOT NULL COMMENT '错误答案数',
  exam_time_seconds INT NOT NULL COMMENT '考试用时（秒）',
  created_at DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  CONSTRAINT FK_EXAM_RECORD_USER FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试记录表';

-- 创建用户单词错误记录表
CREATE TABLE user_word_error (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  word_id BIGINT NOT NULL COMMENT '单词ID',
  error_count INT NOT NULL DEFAULT 0 COMMENT '错误次数',
  updated_at DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  CONSTRAINT FK_USER_WORD_ERROR_USER FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
  CONSTRAINT FK_USER_WORD_ERROR_WORD FOREIGN KEY (word_id) REFERENCES word (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户单词错误记录表';

-- 插入示例用户数据（使用明文密码）
INSERT INTO user (username, password, nick_name, total_score, role, grade, created_at, updated_at) VALUES
('admin', 'admin123', '管理员', 0, 'ADMIN', NULL, NOW(), NOW()),
('testuser', 'test123', '测试用户', 0, 'USER', 1, NOW(), NOW());

-- 插入示例单词数据
INSERT INTO word (english, chinese, grade, unit, created_at, updated_at) VALUES
('apple', '苹果', 1, 1, NOW(), NOW()),
('banana', '香蕉', 1, 1, NOW(), NOW()),
('orange', '橙子', 1, 1, NOW(), NOW()),
('grape', '葡萄', 1, 1, NOW(), NOW()),
('watermelon', '西瓜', 1, 1, NOW(), NOW()),
('strawberry', '草莓', 1, 1, NOW(), NOW()),
('pineapple', '菠萝', 1, 1, NOW(), NOW()),
('mango', '芒果', 1, 1, NOW(), NOW()),
('peach', '桃子', 1, 1, NOW(), NOW()),
('pear', '梨', 1, 1, NOW(), NOW()),
('computer', '计算机', 1, 2, NOW(), NOW()),
('phone', '电话', 1, 2, NOW(), NOW()),
('book', '书', 1, 2, NOW(), NOW()),
('pen', '笔', 1, 2, NOW(), NOW()),
('pencil', '铅笔', 1, 2, NOW(), NOW()),
('desk', '桌子', 1, 2, NOW(), NOW()),
('chair', '椅子', 1, 2, NOW(), NOW()),
('window', '窗户', 1, 2, NOW(), NOW()),
('door', '门', 1, 2, NOW(), NOW()),
('light', '灯', 1, 2, NOW(), NOW());

-- 插入示例考试记录数据
INSERT INTO exam_record (user_id, score, total_questions, correct_answers, wrong_answers, exam_time_seconds, created_at) VALUES
(1, 80, 10, 8, 2, 300, NOW()),
(2, 90, 10, 9, 1, 280, NOW());

-- 插入示例用户单词错误记录数据
INSERT INTO user_word_error (user_id, word_id, error_count, updated_at) VALUES
(1, 1, 2, NOW()),
(1, 3, 1, NOW()),
(2, 2, 1, NOW());

-- 验证数据插入
SELECT 'Users:' as Table_Name;
SELECT id, username, password, role FROM user;

SELECT 'Words:' as Table_Name;
SELECT id, english, chinese, grade, unit FROM word LIMIT 5;

COMMIT;