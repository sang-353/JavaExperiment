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
 * 等额本金还款方式计算器
 * 公式：每月月供 = P/n + (P - 已还本金) * r
 * 其中：P 为本金，n 为总月数，r 为月利率
 */
public class AverageCapital {

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
     * 计算等额本金还款方式的详细信息
     *
     * @param principal   本金（万元）
     * @param yearRate    年利率（%）
     * @param totalMonths 贷款总月数
     * @return 包含总利息、首月月供、末月月供和每月明细的 Map
     */
    public static Map<String, Object> calculate(BigDecimal principal, BigDecimal yearRate, int totalMonths) {
        // 计算月利率（年利率 / 12 / 100）
        BigDecimal monthlyRate = yearRate.divide(new BigDecimal(12 * 100), WORK_SCALE, ROUNDING_MODE);

        // 计算固定月本金 = 本金 / 总月数
        BigDecimal monthlyPrincipalExact = principal.divide(new BigDecimal(totalMonths), WORK_SCALE, ROUNDING_MODE);

        // 构建返回结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("monthlyPrincipal", monthlyPrincipalExact.setScale(SCALE, ROUNDING_MODE));

        // 计算详细的每月还款明细
        List<Map<String, Object>> scheduleList = new ArrayList<>();
        BigDecimal remainingPrincipal = principal.setScale(WORK_SCALE, ROUNDING_MODE);
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal firstMonthPayment = BigDecimal.ZERO;
        BigDecimal lastMonthPayment = BigDecimal.ZERO;

        for (int month = 1; month <= totalMonths; month++) {
            Map<String, Object> monthDetail = new LinkedHashMap<>();

            BigDecimal monthlyInterestExact = remainingPrincipal.multiply(monthlyRate).setScale(WORK_SCALE, ROUNDING_MODE);
            BigDecimal monthlyPaymentExact;
            BigDecimal monthlyPrincipalForMonth;

            if (month == totalMonths) {
                // 最后一期直接结清剩余本金，避免因累计舍入造成负余额
                monthlyPrincipalForMonth = remainingPrincipal;
                monthlyInterestExact = monthlyPrincipalForMonth.signum() == 0
                        ? BigDecimal.ZERO.setScale(WORK_SCALE, ROUNDING_MODE)
                        : monthlyInterestExact;
                monthlyPaymentExact = monthlyPrincipalForMonth.add(monthlyInterestExact).setScale(WORK_SCALE, ROUNDING_MODE);
            } else {
                monthlyPrincipalForMonth = monthlyPrincipalExact;
                monthlyPaymentExact = monthlyPrincipalForMonth.add(monthlyInterestExact).setScale(WORK_SCALE, ROUNDING_MODE);
            }

            // 更新剩余本金
            remainingPrincipal = remainingPrincipal.subtract(monthlyPrincipalForMonth).setScale(WORK_SCALE, ROUNDING_MODE);
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
                remainingPrincipal = BigDecimal.ZERO.setScale(WORK_SCALE, ROUNDING_MODE);
            }
            totalInterest = totalInterest.add(monthlyInterestExact).setScale(WORK_SCALE, ROUNDING_MODE);

            // 记录首月和末月的月供
            BigDecimal monthlyPaymentDisplay = monthlyPaymentExact.setScale(SCALE, ROUNDING_MODE);
            if (month == 1) {
                firstMonthPayment = monthlyPaymentDisplay;
            }
            if (month == totalMonths) {
                lastMonthPayment = monthlyPaymentDisplay;
            }

            monthDetail.put("month", month);
            monthDetail.put("monthlyPayment", monthlyPaymentDisplay);
            monthDetail.put("monthlyPrincipal", monthlyPrincipalForMonth.setScale(SCALE, ROUNDING_MODE));
            monthDetail.put("monthlyInterest", monthlyInterestExact.setScale(SCALE, ROUNDING_MODE));
            monthDetail.put("remainingPrincipal", remainingPrincipal.setScale(SCALE, ROUNDING_MODE));

            scheduleList.add(monthDetail);
        }

        result.put("schedule", scheduleList);
        result.put("firstMonthPayment", firstMonthPayment);
        result.put("lastMonthPayment", lastMonthPayment);
        result.put("totalInterest", totalInterest.setScale(SCALE, ROUNDING_MODE));
        result.put("totalPayment", principal.add(totalInterest).setScale(SCALE, ROUNDING_MODE));

        return result;
    }
}

