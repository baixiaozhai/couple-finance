-- 情侣/家庭财务管理系统数据库设计
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS couple_finance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE couple_finance;

-- ==================== 用户表 ====================
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '加密密码（BCrypt）',
    nickname VARCHAR(50) COMMENT '昵称',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ==================== 家庭表 ====================
CREATE TABLE family (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '家庭ID',
    name VARCHAR(100) NOT NULL COMMENT '家庭名称',
    invite_code VARCHAR(20) NOT NULL UNIQUE COMMENT '邀请码（6位字母数字）',
    created_by BIGINT NOT NULL COMMENT '创建者用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (created_by) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭表';

-- ==================== 家庭成员表 ====================
CREATE TABLE family_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    family_id BIGINT NOT NULL COMMENT '家庭ID',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    UNIQUE KEY uk_user_family (user_id, family_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (family_id) REFERENCES family(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭成员关联表';

-- ==================== 记账表 ====================
CREATE TABLE transaction_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    family_id BIGINT NOT NULL COMMENT '家庭ID',
    user_id BIGINT NOT NULL COMMENT '记录创建者ID',
    amount DECIMAL(12, 2) NOT NULL COMMENT '金额',
    category VARCHAR(50) NOT NULL COMMENT '分类（餐饮、房租、娱乐等）',
    type VARCHAR(20) NOT NULL COMMENT '类型：income-收入, expense-支出',
    payer_id BIGINT NOT NULL COMMENT '实际付款人ID',
    record_date DATE NOT NULL COMMENT '记账日期',
    note VARCHAR(500) COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (family_id) REFERENCES family(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (payer_id) REFERENCES user(id),
    INDEX idx_family_date (family_id, record_date),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记账表';

-- ==================== 预算表 ====================
CREATE TABLE budget (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预算ID',
    family_id BIGINT NOT NULL COMMENT '家庭ID',
    month VARCHAR(7) NOT NULL COMMENT '月份，格式：2026-04',
    category VARCHAR(50) NOT NULL COMMENT '分类',
    amount DECIMAL(12, 2) NOT NULL COMMENT '预算金额',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_family_month_category (family_id, month, category),
    FOREIGN KEY (family_id) REFERENCES family(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预算表';

-- ==================== 初始化数据 ====================
-- 支出分类枚举值（供前端使用）
-- 餐饮、交通、购物、娱乐、住房、医疗、教育、通讯、人情、其他

-- 收入分类枚举值（供前端使用）
-- 工资、奖金、投资收益、兼职、红包、其他

-- ==================== 常用查询示例 ====================
-- 1. 查询某家庭某月支出统计
-- SELECT category, SUM(amount) as total 
-- FROM transaction_record 
-- WHERE family_id = ? AND type = 'expense' AND DATE_FORMAT(record_date, '%Y-%m') = '2026-04'
-- GROUP BY category;

-- 2. 查询预算执行情况
-- SELECT b.category, b.amount as budget, COALESCE(SUM(t.amount), 0) as actual
-- FROM budget b
-- LEFT JOIN transaction_record t ON b.family_id = t.family_id 
--     AND b.category = t.category 
--     AND t.type = 'expense'
--     AND DATE_FORMAT(t.record_date, '%Y-%m') = b.month
-- WHERE b.family_id = ? AND b.month = '2026-04'
-- GROUP BY b.category, b.amount;
