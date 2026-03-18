-- 修复 t_chef_info 表中的厨师数据
-- 如果存在旧数据，先删除
DELETE FROM t_chef_info WHERE user_id = '2';

-- 插入完整的厨师数据
INSERT INTO t_chef_info (
    user_id, 
    real_name, 
    gender, 
    phone, 
    detail_address, 
    avatar_url, 
    qualification_url, 
    experience_years, 
    chef_level, 
    min_price, 
    completed_orders, 
    introduction, 
    audit_status, 
    latitude, 
    longitude, 
    status
) VALUES (
    '2',                              -- user_id (关联 t_user.id)
    '张大厨',                          -- real_name (厨师真实姓名)
    1,                                -- gender (1-男，2-女，0-未知)
    '13800138001',                    -- phone (手机号)
    '四川省成都市高新区天府大道 100 号',   -- detail_address (详细地址)
    'chefs/avatar/zhangdachuf.jpg',   -- avatar_url (头像 URL)
    'chefs/qualification/zhangdachuf_cert.jpg', -- qualification_url (资质证书 URL)
    15,                               -- experience_years (烹饪年限)
    4,                                -- chef_level (厨师等级：4-资深厨师)
    200.00,                           -- min_price (最低服务价格)
    50,                               -- completed_orders (已完成订单数)
    '从事川菜烹饪 15 年，擅长水煮鱼、麻婆豆腐等经典菜品', -- introduction (简介)
    1,                                -- audit_status (审核状态：1-通过)
    30.57269312,                      -- latitude (纬度 - 成都)
    104.06670380,                     -- longitude (经度 - 成都)
    1                                 -- status (状态：1-启用)
);

-- 验证查询
SELECT 
    id,
    user_id,
    real_name,
    gender,
    chef_level,
    min_price,
    completed_orders,
    audit_status,
    status
FROM t_chef_info;
