package com.sang.javaexperiment.JavaProject4.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 等额本息还款方式计算器
 * 公式：每月供额 = P * r * (1+r)^n / ((1+r)^n - 1)
 * 其中：P 为本金，r 为月利率，n 为总月数
 */
public class AverageCapitalPlusInterest {

    /**
     * 精度设置：两位小数
     */
    private static final int SCALE = 2;

    /**
     * 内部运算精度：保留更多小数位，避免累计误差
     */
    private static final int WORK_SCALE = 12;

    /**
     * 四舍五入模式
     */
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 计算等额本息还款方式的详细信息
     *
     * @param principal   本金（万元）
     * @param yearRate    年利率（%）
     * @param totalMonths 贷款总月数
     * @return 包含总利息、月供和每月明细的 Map
     */
    public static Map<String, Object> calculate(BigDecimal principal, BigDecimal yearRate, int totalMonths) {
        // 计算月利率（年利率 / 12 / 100）
        BigDecimal monthlyRate = yearRate.divide(new BigDecimal(12 * 100), WORK_SCALE, ROUNDING_MODE);

        // 计算月供：P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal monthlyPayment = calculateMonthlyPayment(principal, monthlyRate, totalMonths);

        // 构建返回结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("monthlyPayment", monthlyPayment.setScale(SCALE, ROUNDING_MODE));

        // 计算详细的每月还款明细
        List<Map<String, Object>> scheduleList = new ArrayList<>();
        BigDecimal remainingPrincipal = principal.setScale(WORK_SCALE, ROUNDING_MODE);
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (int month = 1; month <= totalMonths; month++) {
            Map<String, Object> monthDetail = new LinkedHashMap<>();

            BigDecimal monthlyInterestExact = remainingPrincipal.multiply(monthlyRate).setScale(WORK_SCALE, ROUNDING_MODE);
            BigDecimal monthlyPrincipalExact;

            if (month == totalMonths) {
                // 最后一期直接结清剩余本金，避免因累计舍入造成负余额
                monthlyPrincipalExact = remainingPrincipal;
                monthlyInterestExact = monthlyPayment.subtract(monthlyPrincipalExact).setScale(WORK_SCALE, ROUNDING_MODE);
            } else {
                monthlyPrincipalExact = monthlyPayment.subtract(monthlyInterestExact).setScale(WORK_SCALE, ROUNDING_MODE);
            }

            // 更新剩余本金
            remainingPrincipal = remainingPrincipal.subtract(monthlyPrincipalExact).setScale(WORK_SCALE, ROUNDING_MODE);
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
                remainingPrincipal = BigDecimal.ZERO.setScale(WORK_SCALE, ROUNDING_MODE);
            }
            totalInterest = totalInterest.add(monthlyInterestExact).setScale(WORK_SCALE, ROUNDING_MODE);

            monthDetail.put("month", month);
            monthDetail.put("monthlyPayment", monthlyPayment.setScale(SCALE, ROUNDING_MODE));
            monthDetail.put("monthlyPrincipal", monthlyPrincipalExact.setScale(SCALE, ROUNDING_MODE));
            monthDetail.put("monthlyInterest", monthlyInterestExact.setScale(SCALE, ROUNDING_MODE));
            monthDetail.put("remainingPrincipal", remainingPrincipal.setScale(SCALE, ROUNDING_MODE));

            scheduleList.add(monthDetail);
        }

        result.put("schedule", scheduleList);
        result.put("totalInterest", totalInterest.setScale(SCALE, ROUNDING_MODE));
        result.put("totalPayment", monthlyPayment.multiply(new BigDecimal(totalMonths)).setScale(SCALE, ROUNDING_MODE));

        return result;
    }

    /**
     * 计算月供金额
     *
     * @param principal   本金
     * @param monthlyRate 月利率
     * @param totalMonths 总月数
     * @return 月供金额
     */
    private static BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal monthlyRate, int totalMonths) {
        // 计算 (1 + r)^n
        BigDecimal rPlusOne = BigDecimal.ONE.add(monthlyRate);
        BigDecimal powerRPlusOne = rPlusOne.pow(totalMonths);

        // 分子：P * r * (1+r)^n
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(powerRPlusOne);

        // 分母：(1+r)^n - 1
        BigDecimal denominator = powerRPlusOne.subtract(BigDecimal.ONE);

        // 月供 = 分子 / 分母
        return numerator.divide(denominator, WORK_SCALE, ROUNDING_MODE);
    }
}

