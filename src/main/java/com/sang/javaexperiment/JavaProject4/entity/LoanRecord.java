package com.sang.javaexperiment.JavaProject4.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 贷款记录实体类
 * 用于映射数据库中的 loan_history 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("loan_history")
public class LoanRecord {

    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 贷款本金（万元），精度为两位小数
     */
    private BigDecimal principal;

    /**
     * 年利率（%），精度为两位小数
     */
    private BigDecimal yearRate;

    /**
     * 贷款月数
     */
    private Integer totalMonths;

    /**
     * 还款方式（等额本息/等额本金）
     */
    private String loanType;

    /**
     * 总利息，精度为两位小数
     */
    private BigDecimal totalInterest;

    /**
     * 计算时间，默认为当前时间
     */
    private LocalDateTime calcTime;
}

