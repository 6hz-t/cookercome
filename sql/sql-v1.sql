1. 统一用户表（核心）
sql
CREATE TABLE `t_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID（唯一主键）',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `phone` varchar(20) NOT NULL COMMENT '手机号（登录账号，唯一）',
  `password` varchar(100) NOT NULL COMMENT '加密密码（BCrypt/SHA256）',
  `avatar` varchar(255) DEFAULT '' COMMENT '用户头像URL',
  `real_name` varchar(50) DEFAULT '' COMMENT '真实姓名',
  `id_card` varchar(20) DEFAULT '' COMMENT '身份证号（厨师必填）',
  `gender` tinyint DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `email` varchar(100) DEFAULT '' COMMENT '邮箱（可选）',
  `role` tinyint NOT NULL DEFAULT 0 COMMENT '角色：0-客户，1-厨师，2-管理员',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '账号状态：1-正常，0-禁用，2-注销',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_username` (`username`),
  KEY `idx_role` (`role`) COMMENT '角色索引，快速筛选客户/厨师'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统一用户表（客户+厨师+管理员）';
2. 角色字典表
sql
CREATE TABLE `t_role` (
  `id` tinyint NOT NULL AUTO_INCREMENT COMMENT '角色ID（对应t_user.role）',
  `role_name` varchar(20) NOT NULL COMMENT '角色标识：customer/chef/admin',
  `role_desc` varchar(100) DEFAULT '' COMMENT '角色描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色字典表（统一管理角色含义）';

-- 初始化角色数据
INSERT INTO `t_role` (`id`, `role_name`, `role_desc`) VALUES
(0, 'customer', '普通用户/客户，下单预约厨师'),
(1, 'chef', '厨师，提供上门烹饪服务'),
(2, 'admin', '平台管理员，负责审核、管理');
3. 厨师专属信息表
sql
CREATE TABLE `t_chef_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '厨师信息ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID（t_user.id）',
  `id_card_front` varchar(255) DEFAULT '' COMMENT '身份证正面照URL',
  `id_card_back` varchar(255) DEFAULT '' COMMENT '身份证背面照URL',
  `experience_years` tinyint DEFAULT 0 COMMENT '烹饪年限',
  `introduction` text DEFAULT '' COMMENT '厨师简介',
  `audit_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态：0-待审核，1-审核通过，2-审核拒绝',
  `audit_remark` varchar(255) DEFAULT '' COMMENT '审核备注',
  `latitude` decimal(10,8) DEFAULT NULL COMMENT '常驻位置纬度',
  `longitude` decimal(11,8) DEFAULT NULL COMMENT '常驻位置经度',
  `service_radius` int NOT NULL DEFAULT 5000 COMMENT '服务半径（米）',
  `price_per_meal` decimal(10,2) DEFAULT 0.00 COMMENT '每餐基础定价',
  `service_status` tinyint NOT NULL DEFAULT 0 COMMENT '服务状态：0-休息，1-可接单',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`) COMMENT '一个用户仅一条厨师信息',
  KEY `idx_chef_location` (`latitude`, `longitude`) COMMENT '地理位置索引，用于附近厨师匹配',
  KEY `idx_audit_status` (`audit_status`) COMMENT '审核状态索引，筛选已审核厨师'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='厨师专属信息表';
4. 菜系字典表
sql
CREATE TABLE `t_cuisine` (
  `id` tinyint NOT NULL AUTO_INCREMENT COMMENT '菜系ID',
  `cuisine_name` varchar(30) NOT NULL COMMENT '菜系名称（川菜/粤菜/鲁菜等）',
  `description` varchar(255) DEFAULT '' COMMENT '菜系描述',
  `sort` tinyint DEFAULT 0 COMMENT '排序值（前端展示顺序）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cuisine_name` (`cuisine_name`) COMMENT '菜系名称唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜系字典表';

-- 初始化常见菜系
INSERT INTO `t_cuisine` (`cuisine_name`, `description`) VALUES
('川菜', '以麻辣、鱼香、怪味等口味著称，注重调味'),
('粤菜', '清、鲜、嫩、滑、爽、香，注重原汁原味'),
('鲁菜', '咸鲜为主，精于制汤，善用爆、扒技法'),
('苏菜', '清鲜平和，咸甜适中，讲究刀工和造型'),
('浙菜', '鲜嫩软滑，清爽不腻，注重原汁原味'),
('闽菜', '清鲜和醇，酸甜适度，擅长红糟调味'),
('湘菜', '香辣、香鲜、软嫩，注重原料入味'),
('徽菜', '重油、重色、重火功，讲究食补');
5. 厨师 - 菜系关联表
sql
CREATE TABLE `t_chef_cuisine` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` bigint NOT NULL COMMENT '厨师用户ID（t_user.id）',
  `cuisine_id` tinyint NOT NULL COMMENT '菜系ID（t_cuisine.id）',
  `is_main` tinyint DEFAULT 0 COMMENT '是否主打菜系：1-是，0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_chef_cuisine` (`user_id`, `cuisine_id`) COMMENT '一个厨师对一个菜系仅关联一次',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_cuisine_id` (`cuisine_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='厨师-菜系关联表（多对多）';
6. 厨师菜品表
sql
CREATE TABLE `t_chef_dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
  `user_id` bigint NOT NULL COMMENT '厨师用户ID（t_user.id）',
  `dish_name` varchar(100) NOT NULL COMMENT '菜品名称',
  `cuisine_id` tinyint NOT NULL COMMENT '所属菜系ID（t_cuisine.id）',
  `dish_type` varchar(50) DEFAULT '' COMMENT '菜品类型：热菜/凉菜/汤品/主食',
  `price` decimal(10,2) DEFAULT 0.00 COMMENT '菜品单价',
  `description` text DEFAULT '' COMMENT '菜品描述',
  `picture` varchar(255) DEFAULT '' COMMENT '菜品图片URL',
  `is_featured` tinyint DEFAULT 0 COMMENT '是否特色菜：1-是，0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '查询厨师所有菜品',
  KEY `idx_cuisine_id` (`cuisine_id`) COMMENT '按菜系查菜品',
  KEY `idx_chef_cuisine` (`user_id`, `cuisine_id`) COMMENT '查询厨师某菜系菜品'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='厨师菜品表';
7. 用户地址表
sql
CREATE TABLE `t_user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID（t_user.id）',
  `receiver` varchar(50) NOT NULL COMMENT '收件人/联系人',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `province` varchar(50) NOT NULL COMMENT '省份',
  `city` varchar(50) NOT NULL COMMENT '城市',
  `district` varchar(50) NOT NULL COMMENT '区县',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `latitude` decimal(10,8) NOT NULL COMMENT '地址纬度',
  `longitude` decimal(11,8) NOT NULL COMMENT '地址经度',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认地址：1-是，0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '查询用户所有地址'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表（客户/厨师均可有）';
8. 订单表
sql
CREATE TABLE `t_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号（唯一，如：20260304123456789）',
  `customer_id` bigint NOT NULL COMMENT '客户用户ID（t_user.id）',
  `chef_id` bigint NOT NULL COMMENT '厨师用户ID（t_user.id）',
  `address_id` bigint NOT NULL COMMENT '服务地址ID（t_user_address.id）',
  `reserve_date` date NOT NULL COMMENT '预约日期',
  `reserve_time` varchar(20) NOT NULL COMMENT '预约时间段（如：11:00-13:00）',
  `dish_requirements` text DEFAULT '' COMMENT '菜品定制要求（如：少辣、不放香菜）',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-厨师接单，3-服务中，4-服务完成，5-订单取消，6-退款中，7-已退款',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `service_start_time` datetime DEFAULT NULL COMMENT '服务开始时间',
  `service_end_time` datetime DEFAULT NULL COMMENT '服务结束时间',
  `remark` varchar(255) DEFAULT '' COMMENT '用户备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_chef_id` (`chef_id`),
  KEY `idx_order_status` (`status`) COMMENT '按状态筛选订单'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
9. 订单菜品表
sql
CREATE TABLE `t_order_dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint NOT NULL COMMENT '关联订单ID（t_order.id）',
  `dish_name` varchar(100) NOT NULL COMMENT '菜品名称（定制后）',
  `cuisine_id` tinyint NOT NULL COMMENT '所属菜系ID',
  `dish_price` decimal(10,2) NOT NULL COMMENT '菜品单价',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '菜品数量',
  `requirements` varchar(255) DEFAULT '' COMMENT '菜品定制要求',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`) COMMENT '查询订单所有菜品'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单菜品表（定制化）';
10. 评价表
sql
CREATE TABLE `t_evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint NOT NULL COMMENT '关联订单ID（t_order.id）',
  `customer_id` bigint NOT NULL COMMENT '评价用户ID（t_user.id）',
  `chef_id` bigint NOT NULL COMMENT '被评价厨师ID（t_user.id）',
  `score` tinyint NOT NULL COMMENT '评分（1-5分）',
  `content` text DEFAULT '' COMMENT '评价内容',
  `pictures` varchar(500) DEFAULT '' COMMENT '评价图片URL（多个用逗号分隔）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`) COMMENT '一个订单仅一次评价',
  KEY `idx_chef_id` (`chef_id`) COMMENT '查询厨师所有评价',
  KEY `idx_score` (`score`) COMMENT '按评分筛选'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务评价表';