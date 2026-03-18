-- ============================================
-- t_chef_info 表结构更新脚本
-- 日期：2026-03-17
-- 说明：更新厨师信息表结构，适配新的实体类字段
-- ============================================

-- 1. 删除旧字段
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS chef_no;
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS work_years;
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS specialty;
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS level;
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS service_count;
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS rating;
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS base_price;
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS online_status;
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS certificates;
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS service_radius;
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS price_per_meal;
ALTER TABLE t_chef_info DROP COLUMN IF EXISTS service_status;

-- 2. 添加新字段
ALTER TABLE t_chef_info 
ADD COLUMN IF NOT EXISTS real_name VARCHAR(50) COMMENT '厨师真实姓名' AFTER user_id,
ADD COLUMN IF NOT EXISTS gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女' AFTER real_name,
ADD COLUMN IF NOT EXISTS id_card_no VARCHAR(18) COMMENT '身份证号码（18 位）' AFTER gender,
ADD COLUMN IF NOT EXISTS phone VARCHAR(20) COMMENT '手机号（支持国际号码）' AFTER id_card_no,
ADD COLUMN IF NOT EXISTS detail_address VARCHAR(255) COMMENT '详细地址（街道、门牌号等）' AFTER id_card_back,
ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(255) COMMENT '厨师头像 URL' AFTER detail_address,
ADD COLUMN IF NOT EXISTS qualification_url VARCHAR(500) COMMENT '厨师资质证书 URL' AFTER avatar_url;

-- 3. 修改字段类型（latitude/longitude 改为 BigDecimal 兼容的 DECIMAL）
ALTER TABLE t_chef_info 
MODIFY COLUMN latitude DECIMAL(10, 8) COMMENT '常驻位置纬度（-90~90）',
MODIFY COLUMN longitude DECIMAL(11, 8) COMMENT '常驻位置经度（-180~180）';

-- 4. 添加 status 字段（如果不存在）
ALTER TABLE t_chef_info 
ADD COLUMN IF NOT EXISTS status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用' AFTER update_time;

-- 5. 添加索引
CREATE INDEX IF NOT EXISTS idx_user_id ON t_chef_info(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_status ON t_chef_info(audit_status);
CREATE INDEX IF NOT EXISTS idx_chef_location ON t_chef_info(latitude, longitude);

-- 6. 更新表注释
ALTER TABLE t_chef_info COMMENT '厨师专属信息表';
