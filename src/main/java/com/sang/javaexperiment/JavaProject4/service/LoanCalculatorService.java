package com.sang.javaexperiment.JavaProject4.service;

import com.sang.javaexperiment.JavaProject4.entity.LoanRecord;
import com.sang.javaexperiment.JavaProject4.mapper.LoanRecordMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 贷款计算业务逻辑服务类
 * 负责协调计算过程和数据库持久化
 */
@Service
public class LoanCalculatorService {

    private final LoanRecordMapper loanRecordMapper;
    private final DataSource dataSource;

    public LoanCalculatorService(LoanRecordMapper loanRecordMapper, DataSource dataSource) {
        this.loanRecordMapper = loanRecordMapper;
        this.dataSource = dataSource;
    }

    /**
     * 执行贷款计算并保存到数据库
     *
     * @param principal   本金（万元）
     * @param yearRate    年利率（%）
     * @param totalMonths 贷款总月数
     * @param loanType    还款方式（等额本息/等额本金）
     * @return 计算结果 Map，包含月供、总利息、还款明细等
     */
    public Map<String, Object> calculateAndSave(BigDecimal principal, BigDecimal yearRate, int totalMonths, String loanType) {
        Map<String, Object> result;

        // 根据还款方式选择对应的计算器
        if ("等额本息".equals(loanType)) {
            result = AverageCapitalPlusInterest.calculate(principal, yearRate, totalMonths);
        } else if ("等额本金".equals(loanType)) {
            result = AverageCapital.calculate(principal, yearRate, totalMonths);
        } else {
            throw new IllegalArgumentException("不支持的还款方式：" + loanType);
        }

        // 从计算结果中提取总利息
        BigDecimal totalInterest = (BigDecimal) result.get("totalInterest");

        // 创建贷款记录实体
        LoanRecord record = new LoanRecord();
        record.setPrincipal(principal);
        record.setYearRate(yearRate);
        record.setTotalMonths(totalMonths);
        record.setLoanType(loanType);
        record.setTotalInterest(totalInterest);
        record.setCalcTime(LocalDateTime.now());

        // 保存到数据库
        loanRecordMapper.insert(record);

        return result;
    }

    /**
     * 获取所有历史计算记录
     *
     * @return 贷款记录列表
     */
    public List<LoanRecord> getAllRecords() {
        QueryWrapper<LoanRecord> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("calc_time");
        return loanRecordMapper.selectList(wrapper);
    }

    /**
     * 根据 ID 删除一条记录
     *
     * @param id 记录 ID
     * @return 是否删除成功
     */
    public boolean deleteRecord(Long id) {
        return loanRecordMapper.deleteById(id) > 0;
    }

    /**
     * 测试数据库连接
     */
    public boolean testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(3);
        } catch (SQLException ex) {
            System.err.println("[ERROR] 数据库连接测试失败: " + ex.getMessage());
            return false;
        }
    }
}
