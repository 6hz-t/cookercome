-- ============================================
-- CookerCome 厨师上门服务平台 - MySQL 数据库结构
-- 版本：v2.0
-- 字符集：utf8mb4 / COLLATE: utf8mb4_unicode_ci
-- 说明：所有表使用逻辑外键关联，无物理外键约束
-- ============================================

-- ============================================
-- 👥 用户体系表（3 张）
-- ============================================

-- --------------------------------------------
-- 表名：t_chef_cuisine
-- 描述：厨师 - 菜系关联表（多对多关系）
-- 用途：建立厨师与菜系的多对多关系，支持查询厨师擅长菜系
-- SQL 示例：SELECT * FROM t_chef_cuisine WHERE user_id = ?;
-- --------------------------------------------
-- auto-generated definition
create table t_chef_cuisine
(
    id          bigint auto_increment comment '关联 ID'
        primary key,
    user_id     bigint                             not null comment '厨师用户 ID（逻辑外键：t_user.id）',
    cuisine_id  tinyint                            not null comment '菜系 ID（逻辑外键：t_cuisine.id）',
    is_main     tinyint  default 0                 null comment '是否主打菜系：1-是，0-否',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint uk_chef_cuisine
        unique (user_id, cuisine_id) comment '一个厨师对一个菜系仅关联一次'
)
    comment '厨师 - 菜系关联表（多对多）' collate = utf8mb4_unicode_ci;

create index idx_cuisine_id
    on t_chef_cuisine (cuisine_id);

create index idx_user_id
    on t_chef_cuisine (user_id);

-- --------------------------------------------
-- 表名：t_chef_dish
-- 描述：厨师菜品表
-- 用途：存储厨师的菜品信息，支持按菜系、特色筛选
-- SQL 示例：SELECT * FROM t_chef_dish WHERE user_id = ? ORDER BY is_featured DESC;
-- --------------------------------------------
-- auto-generated definition
create table t_chef_dish
(
    id          bigint auto_increment comment '菜品 ID'
        primary key,
    user_id     bigint                                   not null comment '厨师用户 ID（逻辑外键：t_user.id）',
    dish_name   varchar(100)                             not null comment '菜品名称',
    cuisine_id  tinyint                                  not null comment '所属菜系 ID（逻辑外键：t_cuisine.id）',
    dish_type   varchar(50)    default ''                null comment '菜品类型：热菜/凉菜/汤品/主食',
    price       decimal(10, 2) default 0.00              null comment '菜品单价',
    description text                                     null comment '菜品描述',
    picture     varchar(255)   default ''                null comment '菜品图片 URL',
    is_featured tinyint        default 0                 null comment '是否特色菜：1-是，0-否',
    create_time datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '厨师菜品表' collate = utf8mb4_unicode_ci;

create index idx_chef_cuisine
    on t_chef_dish (user_id, cuisine_id)
    comment '查询厨师某菜系菜品';

create index idx_cuisine_id
    on t_chef_dish (cuisine_id)
    comment '按菜系查菜品';

create index idx_user_id
    on t_chef_dish (user_id)
    comment '查询厨师所有菜品';

-- --------------------------------------------
-- 表名：t_chef_info
-- 描述：厨师专属信息表
-- 用途：存储厨师个人信息、审核状态、位置等
-- 核心字段：user_id（关联 t_user）、audit_status（审核状态）、latitude/longitude（位置）
-- 索引：idx_chef_location（地理位置索引，用于附近厨师匹配）
-- ⚠️ 注意：查询时使用 WHERE user_id = ? 而不是 WHERE id = ?
-- SQL 示例：SELECT * FROM t_chef_info WHERE user_id = ? AND audit_status = 1;
-- --------------------------------------------
-- auto-generated definition
create table t_chef_info
(
    id               bigint auto_increment comment '厨师信息 ID'
        primary key,
    user_id          bigint                                   not null comment '关联用户 ID（逻辑外键：t_user.id）',
    real_name        varchar(50)    default ''                null comment '厨师真实姓名',
    gender           tinyint        default 0                 null comment '性别：0-未知，1-男，2-女',
    id_card_no       varchar(18)    default ''                null comment '身份证号码（18 位）',
    phone            varchar(20)    default ''                null comment '手机号（支持国际号码）',
    id_card_front    varchar(255)   default ''                null comment '身份证正面照 URL',
    id_card_back     varchar(255)   default ''                null comment '身份证背面照 URL',
    detail_address   varchar(255)   default ''                null comment '详细地址（街道、门牌号等）',
    avatar_url       varchar(255)   default ''                null comment '厨师头像 URL',
    qualification_url varchar(500)   default ''                null comment '厨师资质证书 URL',
    experience_years tinyint        default 0                 null comment '烹饪年限',
    introduction     text                                     null comment '厨师简介',
    audit_status     tinyint        default 0                 not null comment '审核状态：0-待审核，1-审核通过，2-审核拒绝',
    audit_remark     varchar(255)   default ''                null comment '审核备注',
    latitude         decimal(10, 8)                           null comment '常驻位置纬度（-90~90）',
    longitude        decimal(11, 8)                           null comment '常驻位置经度（-180~180）',
    status           tinyint        default 1                 null comment '状态：0-禁用，1-启用',
    create_time      datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_user_id
        unique (user_id) comment '一个用户仅一条厨师信息'
)
    comment '厨师专属信息表' collate = utf8mb4_unicode_ci;

create index idx_user_id
    on t_chef_info (user_id)
    comment '查询厨师信息';

create index idx_audit_status
    on t_chef_info (audit_status)
    comment '审核状态索引，筛选已审核厨师';

create index idx_chef_location
    on t_chef_info (latitude, longitude)
    comment '地理位置索引，用于附近厨师匹配';

-- --------------------------------------------
-- 表名：t_coupon
-- 描述：优惠券表
-- 用途：存储平台发放的优惠券信息，支持满减和折扣两种类型
-- 核心字段：coupon_type（0-满减，1-折扣）、discount_amount（优惠金额）、discount_rate（折扣率）
-- SQL 示例：SELECT * FROM t_coupon WHERE status = 1 AND valid_from <= NOW() AND valid_to >= NOW();
-- --------------------------------------------
-- auto-generated definition
create table t_coupon
(
    id              bigint auto_increment comment '优惠券 ID'
        primary key,
    coupon_name     varchar(50)                              not null comment '优惠券名称',
    coupon_type     tinyint        default 0                 not null comment '优惠券类型：0-满减，1-折扣',
    discount_amount decimal(10, 2) default 0.00              null comment '优惠金额（满减用）',
    discount_rate   decimal(3, 2)  default 0.00              null comment '折扣率（折扣用，如 0.85 表示 85 折）',
    min_purchase    decimal(10, 2) default 0.00              null comment '最低消费金额',
    valid_from      date                                     not null comment '有效期开始',
    valid_to        date                                     not null comment '有效期结束',
    total_count     int            default 0                 not null comment '发放总量',
    remain_count    int            default 0                 not null comment '剩余数量',
    status          tinyint        default 1                 not null comment '状态：1-可用，0-停用',
    create_time     datetime       default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '优惠券表' collate = utf8mb4_unicode_ci;

create index idx_status
    on t_coupon (status)
    comment '筛选可用优惠券';

-- --------------------------------------------
-- 表名：t_cuisine
-- 描述：菜系字典表
-- 用途：存储中国八大菜系等字典数据，供厨师选择擅长菜系
-- 初始化数据：川菜、粤菜、鲁菜、苏菜、浙菜、闽菜、湘菜、徽菜
-- SQL 示例：SELECT * FROM t_cuisine WHERE status = 1 ORDER BY sort;
-- --------------------------------------------
-- auto-generated definition
create table t_cuisine
(
    id           tinyint auto_increment comment '菜系 ID'
        primary key,
    cuisine_name varchar(30)             not null comment '菜系名称（川菜/粤菜/鲁菜等）',
    description  varchar(255) default '' null comment '菜系描述',
    sort         tinyint      default 0  null comment '排序值（前端展示顺序）',
    status       tinyint      default 1  not null comment '状态：1-启用，0-禁用',
    constraint uk_cuisine_name
        unique (cuisine_name) comment '菜系名称唯一'
)
    comment '菜系字典表' collate = utf8mb4_unicode_ci;

-- --------------------------------------------
-- 表名：t_customer_coupon
-- 描述：用户优惠券表
-- 用途：存储客户领取的优惠券，跟踪使用状态
-- 核心字段：status（0-未使用，1-已使用，2-已过期）、order_no（使用优惠券的订单编号）
-- SQL 示例：SELECT * FROM t_customer_coupon WHERE customer_id = ? AND status = 0;
-- --------------------------------------------
-- auto-generated definition
create table t_customer_coupon
(
    id          bigint auto_increment comment '用户优惠券 ID'
        primary key,
    customer_id bigint                             not null comment '客户用户 ID（逻辑外键：t_user.id）',
    coupon_id   bigint                             not null comment '优惠券 ID（逻辑外键：t_coupon.id）',
    status      tinyint  default 0                 not null comment '状态：0-未使用，1-已使用，2-已过期',
    used_time   datetime                           null comment '使用时间',
    order_no    varchar(32)                        null comment '使用优惠券的订单编号',
    get_time    datetime default CURRENT_TIMESTAMP not null comment '领取时间'
)
    comment '用户优惠券表' collate = utf8mb4_unicode_ci;

create index idx_customer_id
    on t_customer_coupon (customer_id)
    comment '查询客户所有优惠券';

create index idx_status
    on t_customer_coupon (status)
    comment '筛选未使用优惠券';

-- --------------------------------------------
-- 表名：t_customer_favorite
-- 描述：收藏夹表（客户收藏厨师）
-- 用途：存储客户收藏的厨师，支持唯一性约束（一个客户对一个厨师仅能收藏一次）
-- SQL 示例：SELECT * FROM t_customer_favorite WHERE customer_id = ?;
-- --------------------------------------------
-- auto-generated definition
create table t_customer_favorite
(
    id          bigint auto_increment comment '收藏 ID'
        primary key,
    customer_id bigint                             not null comment '客户用户 ID（逻辑外键：t_user.id）',
    chef_id     bigint                             not null comment '厨师用户 ID（逻辑外键：t_user.id）',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint uk_customer_chef
        unique (customer_id, chef_id) comment '一个客户对一个厨师仅能收藏一次'
)
    comment '收藏夹表（客户收藏厨师）' collate = utf8mb4_unicode_ci;

create index idx_customer_id
    on t_customer_favorite (customer_id)
    comment '查询客户所有收藏';

-- --------------------------------------------
-- 表名：t_customer_info
-- 描述：客户专属信息表
-- 用途：存储客户的详细个人信息、会员等级、积分、订单统计等
-- 核心字段：user_id（关联 t_user）、username（冗余字段）、member_level（会员等级）、points（积分）
-- ⚠️ 注意：查询时使用 WHERE user_id = ? 而不是 WHERE id = ?
-- JWT Token 中存储的是 user_id，不是 id
-- SQL 示例：SELECT * FROM t_customer_info WHERE user_id = ?;
-- --------------------------------------------
-- auto-generated definition
create table t_customer_info
(
    id               bigint auto_increment comment '客户信息 ID'
        primary key,
    user_id          bigint                                  not null comment '关联用户 ID（逻辑外键：t_user.id）',
    username         varchar(50)   default ''                null comment '用户名（冗余字段，便于查询）',
    avatar           varchar(255)  default ''                null comment '头像 URL',
    real_name        varchar(50)   default ''                null comment '真实姓名',
    gender           tinyint       default 0                 null comment '性别：0-未知，1-男，2-女',
    email            varchar(100)  default ''                null comment '邮箱',
    birthday         date                                    null comment '生日',
    member_level     tinyint       default 0                 null comment '会员等级：0-普通，1-白银，2-黄金，3-铂金，4-钻石',
    points           int           default 0                 null comment '积分',
    total_orders     int           default 0                 null comment '总订单数',
    completed_orders int           default 0                 null comment '已完成订单数',
    average_rating   decimal(2, 1) default 0.0               null comment '平均评分',
    create_time      datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_user_id
        unique (user_id) comment '一个用户仅一条客户信息'
)
    comment '客户专属信息表' collate = utf8mb4_unicode_ci;

create index idx_user_id
    on t_customer_info (user_id);

create index idx_username
    on t_customer_info (username);

-- --------------------------------------------
-- 表名：t_evaluation
-- 描述：服务评价表
-- 用途：存储客户对厨师服务的评价，一个订单仅一次评价
-- 核心字段：order_id（唯一）、score（评分 1-5 分）、pictures（评价图片 URL）
-- SQL 示例：SELECT * FROM t_evaluation WHERE chef_id = ? ORDER BY create_time DESC;
-- --------------------------------------------
-- auto-generated definition
create table t_evaluation
(
    id          bigint auto_increment comment '评价 ID'
        primary key,
    order_id    bigint                                 not null comment '关联订单 ID（逻辑外键：t_order.id）',
    customer_id bigint                                 not null comment '评价用户 ID（逻辑外键：t_user.id）',
    chef_id     bigint                                 not null comment '被评价厨师 ID（逻辑外键：t_user.id）',
    score       tinyint                                not null comment '评分（1-5 分）',
    content     text                                   null comment '评价内容',
    pictures    varchar(500) default ''                null comment '评价图片 URL（多个用逗号分隔）',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_order_id
        unique (order_id) comment '一个订单仅一次评价'
)
    comment '服务评价表' collate = utf8mb4_unicode_ci;

create index idx_chef_id
    on t_evaluation (chef_id)
    comment '查询厨师所有评价';

create index idx_score
    on t_evaluation (score)
    comment '按评分筛选';

-- ============================================
-- 🏠 地址与订单表（3 张）
-- ============================================

-- --------------------------------------------
-- 表名：t_order
-- 描述：订单表
-- 用途：存储客户预约厨师服务的订单信息，核心业务表
-- 核心字段：order_no（唯一）、customer_id（客户）、chef_id（厨师）、status（订单状态）
-- 订单状态：0-待支付，1-已支付，2-厨师接单，3-服务中，4-服务完成，5-订单取消，6-退款中，7-已退款
-- SQL 示例：SELECT * FROM t_order WHERE customer_id = ? ORDER BY create_time DESC;
-- --------------------------------------------
-- auto-generated definition
create table t_order
(
    id                 bigint auto_increment comment '订单 ID'
        primary key,
    order_no           varchar(32)                            not null comment '订单编号（唯一，如：20260304123456789）',
    customer_id        bigint                                 not null comment '客户用户 ID（逻辑外键：t_user.id）',
    chef_id            bigint                                 not null comment '厨师用户 ID（逻辑外键：t_user.id）',
    address_id         bigint                                 not null comment '服务地址 ID（逻辑外键：t_user_address.id）',
    reserve_date       date                                   not null comment '预约日期',
    reserve_time       varchar(20)                            not null comment '预约时间段（如：11:00-13:00）',
    dish_requirements  text                                   null comment '菜品定制要求（如：少辣、不放香菜）',
    total_amount       decimal(10, 2)                         not null comment '订单总金额',
    status             tinyint      default 0                 not null comment '订单状态：0-待支付，1-已支付，2-厨师接单，3-服务中，4-服务完成，5-订单取消，6-退款中，7-已退款',
    payment_time       datetime                               null comment '支付时间',
    service_start_time datetime                               null comment '服务开始时间',
    service_end_time   datetime                               null comment '服务结束时间',
    remark             varchar(255) default ''                null comment '用户备注',
    create_time        datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time        datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_order_no
        unique (order_no)
)
    comment '订单表' collate = utf8mb4_unicode_ci;

create index idx_chef_id
    on t_order (chef_id);

create index idx_customer_id
    on t_order (customer_id);

create index idx_order_status
    on t_order (status)
    comment '按状态筛选订单';

-- --------------------------------------------
-- 表名：t_order_dish
-- 描述：订单菜品表（定制化）
-- 用途：存储订单中的具体菜品信息（支持定制化）
-- SQL 示例：SELECT * FROM t_order_dish WHERE order_id = ?;
-- --------------------------------------------
-- auto-generated definition
create table t_order_dish
(
    id           bigint auto_increment comment 'ID'
        primary key,
    order_id     bigint                                 not null comment '关联订单 ID（逻辑外键：t_order.id）',
    dish_name    varchar(100)                           not null comment '菜品名称（定制后）',
    cuisine_id   tinyint                                not null comment '所属菜系 ID（逻辑外键：t_cuisine.id）',
    dish_price   decimal(10, 2)                         not null comment '菜品单价',
    quantity     int          default 1                 not null comment '菜品数量',
    requirements varchar(255) default ''                null comment '菜品定制要求',
    create_time  datetime     default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '订单菜品表（定制化）' collate = utf8mb4_unicode_ci;

create index idx_order_id
    on t_order_dish (order_id)
    comment '查询订单所有菜品';

-- --------------------------------------------
-- 表名：t_review
-- 描述：评价表（带回复功能）
-- 用途：存储客户评价及厨师回复（可能是 t_evaluation 的替代或补充）
-- 核心字段：reply（厨师回复）、reply_time（回复时间）、status（0-待回复，1-已回复）
-- SQL 示例：SELECT * FROM t_review WHERE chef_id = ? AND status = 0;
-- --------------------------------------------
-- auto-generated definition
create table t_review
(
    id          bigint auto_increment comment '评价 ID'
        primary key,
    order_id    bigint                             not null comment '订单 ID（逻辑外键：t_order.id）',
    customer_id bigint                             not null comment '客户用户 ID',
    chef_id     bigint                             not null comment '厨师用户 ID',
    rating      tinyint                            not null comment '评分：1-5 分',
    content     text                               null comment '评价内容',
    reply       text                               null comment '厨师回复',
    reply_time  datetime                           null comment '回复时间',
    status      tinyint  default 0                 not null comment '状态：0-待回复，1-已回复',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '评价表' collate = utf8mb4_unicode_ci;

create index idx_chef_id
    on t_review (chef_id)
    comment '查询厨师所有评价';

create index idx_order_id
    on t_review (order_id);

-- --------------------------------------------
-- 表名：t_user
-- 描述：统一用户表（核心主表）
-- 用途：存储所有用户的登录凭证和角色信息，是整个系统的核心表
-- 核心字段：phone（手机号，登录账号）、password（BCrypt 加密）、role（角色：0-客户，1-厨师，2-管理员）
-- ⚠️ 注意：使用 phone 字段作为登录账号，不是 username
-- SQL 示例：SELECT * FROM t_user WHERE phone = ? AND password = ?;
-- --------------------------------------------
-- auto-generated definition
create table t_user
(
    id          bigint auto_increment comment '用户 ID（唯一主键）'
        primary key,
    phone       varchar(20)                        not null comment '手机号（登录账号，唯一）',
    password    varchar(100)                       not null comment '加密密码（BCrypt）',
    role        tinyint  default 0                 not null comment '角色：0-客户，1-厨师，2-管理员',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_phone
        unique (phone)
)
    comment '统一用户表（客户 + 厨师 + 管理员）' collate = utf8mb4_unicode_ci;

create index idx_role
    on t_user (role);

-- --------------------------------------------
-- 表名：t_user_address
-- 描述：用户地址表（客户/厨师均可有）
-- 用途：存储用户的收货地址，支持经纬度定位
-- 核心字段：user_id（关联用户）、is_default（是否默认地址）、latitude/longitude（经纬度）
-- SQL 示例：SELECT * FROM t_user_address WHERE user_id = ? ORDER BY is_default DESC;
-- --------------------------------------------
-- auto-generated definition
create table t_user_address
(
    id             bigint auto_increment comment '地址 ID'
        primary key,
    user_id        bigint                             not null comment '关联用户 ID（逻辑外键：t_user.id）',
    receiver       varchar(50)                        not null comment '收件人/联系人',
    phone          varchar(20)                        not null comment '联系电话',
    province       varchar(50)                        not null comment '省份',
    city           varchar(50)                        not null comment '城市',
    district       varchar(50)                        not null comment '区县',
    detail_address varchar(255)                       not null comment '详细地址',
    latitude       decimal(10, 8) default null         null comment '地址纬度',
    longitude      decimal(11, 8) default null         null comment '地址经度',
    is_default     tinyint  default 0                 not null comment '是否默认地址：1-是，0-否',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '用户地址表（客户/厨师均可有）' collate = utf8mb4_unicode_ci;

create index idx_user_id
    on t_user_address (user_id)
    comment '查询用户所有地址';

-- ============================================
-- 💡 核心设计要点总结
-- ============================================
-- 1. 统一用户体系：所有用户（客户/厨师/管理员）共享 t_user 表，通过 role 字段区分
-- 2. 主从表结构：t_user 是主表，t_customer_info 和 t_chef_info 是从表，通过 user_id 关联
-- 3. 逻辑外键：所有表使用逻辑外键关联，无物理外键约束，提高灵活性
-- 4. JWT Token: Token 中存储的是 user_id（t_user.id），查询从表时需用 WHERE user_id = ?
-- 5. 唯一性约束:
--    - t_user.phone 唯一（登录账号）
--    - t_customer_info.user_id 唯一（一个用户一条客户信息）
--    - t_chef_info.user_id 唯一（一个用户一条厨师信息）
--    - t_order.order_no 唯一（订单编号）
--    - t_evaluation.order_id 唯一（一个订单一次评价）
-- 6. 地理定位：厨师和地址都存储经纬度，支持基于位置的搜索（附近厨师）
-- 7. 审核机制：厨师需要审核（audit_status），只有审核通过的才能接单
--
-- 🔑 关键 SQL 示例：
-- ① 登录：SELECT * FROM t_user WHERE phone = ? AND password = ?;
-- ② 获取客户信息：SELECT * FROM t_customer_info WHERE user_id = ?;
-- ③ 查询厨师擅长菜系：SELECT c.* FROM t_cuisine c JOIN t_chef_cuisine cc ON c.id = cc.cuisine_id WHERE cc.user_id = ?;
-- ④ 查询附近厨师：使用 Haversine 公式计算距离
-- ⑤ 客户下单：INSERT INTO t_order (order_no, customer_id, chef_id, ...) VALUES (?, ?, ?, ...);
-- ============================================



