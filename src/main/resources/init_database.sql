-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS loan_calculator;

-- 使用数据库
USE loan_calculator;

-- 创建贷款历史表
CREATE TABLE IF NOT EXISTS loan_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    principal DECIMAL(15, 2) NOT NULL COMMENT '贷款本金（万元）',
    year_rate DECIMAL(5, 2) NOT NULL COMMENT '年利率（%）',
    total_months INT NOT NULL COMMENT '贷款月数',
    loan_type VARCHAR(20) NOT NULL COMMENT '还款方式（等额本息/等额本金）',
    total_interest DECIMAL(15, 2) NOT NULL COMMENT '总利息',
    calc_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建索引以提升查询性能
CREATE INDEX idx_calc_time ON loan_history(calc_time DESC);

