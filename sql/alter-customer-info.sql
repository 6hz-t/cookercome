-- ============================================
-- t_customer_info 表结构修改
-- 添加 username 字段用于存储用户名
-- ============================================

-- 添加 username 字段
ALTER TABLE `t_customer_info` 
ADD COLUMN `username` varchar(50) DEFAULT '' COMMENT '用户名（冗余字段，便于查询）' 
AFTER `user_id`;

-- 添加索引
ALTER TABLE `t_customer_info` 
ADD INDEX `idx_username` (`username`);
