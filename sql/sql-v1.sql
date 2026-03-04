完整数据库表结构设计
1. 用户表 (t_user)
存储平台普通用户的基础信息
sql
CREATE TABLE `t_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `phone` varchar(20) NOT NULL COMMENT '手机号（唯一）',
  `password` varchar(100) NOT NULL COMMENT '加密后的密码',
  `avatar` varchar(255) DEFAULT '' COMMENT '用户头像',
  `real_name` varchar(50) DEFAULT '' COMMENT '真实姓名',
  `id_card` varchar(20) DEFAULT '' COMMENT '身份证号（可选）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
2. 厨师表 (t_chef)
存储厨师的基础信息和审核状态，核心字段包含位置信息
sql
CREATE TABLE `t_chef` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '厨师ID',
  `name` varchar(50) NOT NULL COMMENT '厨师姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号（唯一）',
  `password` varchar(100) NOT NULL COMMENT '加密后的密码',
  `avatar` varchar(255) DEFAULT '' COMMENT '厨师头像',
  `id_card` varchar(20) NOT NULL COMMENT '身份证号',
  `id_card_front` varchar(255) NOT NULL COMMENT '身份证正面照',
  `id_card_back` varchar(255) NOT NULL COMMENT '身份证背面照',
  `cuisine_types` varchar(100) NOT NULL COMMENT '擅长菜系（多个用逗号分隔：川菜,粤菜）',
  `experience_years` tinyint DEFAULT 0 COMMENT '烹饪年限',
  `introduction` text DEFAULT '' COMMENT '厨师简介',
  `audit_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态：0-待审核，1-审核通过，2-审核拒绝',
  `audit_remark` varchar(255) DEFAULT '' COMMENT '审核备注',
  `latitude` decimal(10,8) NOT NULL COMMENT '纬度（当前常驻位置）',
  `longitude` decimal(11,8) NOT NULL COMMENT '经度（当前常驻位置）',
  `service_radius` int NOT NULL DEFAULT 5000 COMMENT '服务半径（米）',
  `price_per_meal` decimal(10,2) NOT NULL COMMENT '每餐基础定价',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-可接单，0-休息/暂停',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_chef_phone` (`phone`),
  KEY `idx_chef_location` (`latitude`, `longitude`) COMMENT '地理位置索引，用于附近厨师查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='厨师表';
3. 厨师菜品表 (t_chef_dish)
存储厨师的特色菜品，支持用户查看和定制
sql
CREATE TABLE `t_chef_dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
  `chef_id` bigint NOT NULL COMMENT '关联厨师ID',
  `dish_name` varchar(100) NOT NULL COMMENT '菜品名称',
  `dish_type` varchar(50) DEFAULT '' COMMENT '菜品类型（热菜/凉菜/汤品）',
  `price` decimal(10,2) DEFAULT 0.00 COMMENT '菜品单价（可选）',
  `description` text DEFAULT '' COMMENT '菜品描述',
  `picture` varchar(255) DEFAULT '' COMMENT '菜品图片',
  `is_featured` tinyint DEFAULT 0 COMMENT '是否特色菜：1-是，0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_chef_id` (`chef_id`) COMMENT '厨师ID索引，用于查询厨师的菜品'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='厨师菜品表';
4. 用户地址表 (t_user_address)
存储用户的收货 / 服务地址，包含地理坐标
sql
CREATE TABLE `t_user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `receiver` varchar(50) NOT NULL COMMENT '收件人',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `province` varchar(50) NOT NULL COMMENT '省份',
  `city` varchar(50) NOT NULL COMMENT '城市',
  `district` varchar(50) NOT NULL COMMENT '区县',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `latitude` decimal(10,8) NOT NULL COMMENT '纬度',
  `longitude` decimal(11,8) NOT NULL COMMENT '经度',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认地址：1-是，0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';
5. 订单表 (t_order)
存储服务订单的核心信息，关联用户、厨师、地址
sql
CREATE TABLE `t_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号（唯一）',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `chef_id` bigint NOT NULL COMMENT '厨师ID',
  `address_id` bigint NOT NULL COMMENT '服务地址ID（关联t_user_address）',
  `reserve_date` date NOT NULL COMMENT '预约日期',
  `reserve_time` varchar(20) NOT NULL COMMENT '预约时间段（如：11:00-13:00）',
  `dish_requirements` text DEFAULT '' COMMENT '菜品定制要求',
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
  KEY `idx_user_id` (`user_id`),
  KEY `idx_chef_id` (`chef_id`),
  KEY `idx_order_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
6. 订单菜品表 (t_order_dish)
存储订单中具体的菜品信息（定制化菜品）
sql
CREATE TABLE `t_order_dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `dish_name` varchar(100) NOT NULL COMMENT '菜品名称',
  `dish_price` decimal(10,2) NOT NULL COMMENT '菜品单价',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `requirements` varchar(255) DEFAULT '' COMMENT '菜品定制要求（如：少辣、不放香菜）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单菜品表';
7. 评价表 (t_evaluation)
存储用户对厨师服务的评价
sql
CREATE TABLE `t_evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `user_id` bigint NOT NULL COMMENT '评价用户ID',
  `chef_id` bigint NOT NULL COMMENT '被评价厨师ID',
  `score` tinyint NOT NULL COMMENT '评分（1-5分）',
  `content` text DEFAULT '' COMMENT '评价内容',
  `pictures` varchar(500) DEFAULT '' COMMENT '评价图片（多个用逗号分隔）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`) COMMENT '一个订单只能评价一次',
  KEY `idx_chef_id` (`chef_id`) COMMENT '厨师ID索引，用于查询厨师的评价'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';