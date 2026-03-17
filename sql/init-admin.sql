-- 创建管理员账号
-- 手机号：13800138000
-- 密码：123456（BCrypt 加密）
INSERT INTO t_user (phone, password, role, create_time, update_time)
VALUES ('13800138000', '$2a$10$37hDi87a7Kv.1LB0Dg5eFe8jK/EcEthFkBxbKjrjuFlthpxzMHy4O', 2, NOW(), NOW());

-- 创建管理员信息（如果 t_admin_info 表存在）
INSERT INTO t_admin_info (user_id, real_name, status, create_time, update_time)
SELECT id, '超级管理员', 1, NOW(), NOW()
FROM t_user
WHERE phone = '13800138000'
AND NOT EXISTS (SELECT 1 FROM t_admin_info WHERE user_id = id);
