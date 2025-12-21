-- MySQL数据库示例数据

-- 设置客户端连接字符集
SET NAMES utf8mb4;

-- 插入示例用户数据
-- 密码使用明文存储（临时解决401问题）
-- 原始密码: admin123 和 test123
INSERT INTO `englishwords`.`user` (`username`, `password`, `nick_name`, `total_score`, `role`, `grade`, `created_at`, `updated_at`) VALUES
('admin', 'admin123', '管理员', 0, 'ADMIN', NULL, NOW(), NOW()),
('testuser', 'test123', '测试用户', 0, 'USER', 1, NOW(), NOW());

-- 插入示例单词数据
INSERT INTO `englishwords`.`word` (`english`, `chinese`, `grade`, `unit`, `created_at`, `updated_at`) VALUES
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
INSERT INTO `englishwords`.`exam_record` (`user_id`, `score`, `total_questions`, `correct_answers`, `wrong_answers`, `exam_time_seconds`, `created_at`) VALUES
(1, 80, 10, 8, 2, 300, NOW()),
(2, 90, 10, 9, 1, 280, NOW());

-- 插入示例用户单词错误记录数据
INSERT INTO `englishwords`.`user_word_error` (`user_id`, `word_id`, `error_count`, `updated_at`) VALUES
(1, 1, 2, NOW()),
(1, 3, 1, NOW()),
(2, 2, 1, NOW());