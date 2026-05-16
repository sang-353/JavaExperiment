package com.sang.javaexperiment.JavaProject4;

import com.sang.javaexperiment.JavaProject4.service.AverageCapital;
import com.sang.javaexperiment.JavaProject4.service.AverageCapitalPlusInterest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 贷款计算器本地测试类
 * 用于离线测试计算逻辑，无需数据库和 Spring 框架
 */
public class LoanCalculatorTest {

    public static void main(String[] args) {
        System.out.println("========== 购房贷款计算器测试 ==========\n");

        // 测试参数
        BigDecimal principal = new BigDecimal("100.00");      // 100 万元
        BigDecimal yearRate = new BigDecimal("4.90");         // 4.90%
        int totalMonths = 360;                               // 30 年

        // 测试等额本息
        testAverageCapitalPlusInterest(principal, yearRate, totalMonths);

        System.out.println("\n");

        // 测试等额本金
        testAverageCapital(principal, yearRate, totalMonths);
    }

    /**
     * 测试等额本息计算
     */
    private static void testAverageCapitalPlusInterest(BigDecimal principal, BigDecimal yearRate, int totalMonths) {
        System.out.println("【等额本息计算测试】");
        System.out.println("----------------------------");
        System.out.println("贷款本金：" + principal + " 万元");
        System.out.println("年利率：" + yearRate + " %");
        System.out.println("贷款月数：" + totalMonths + " 个月（" + (totalMonths / 12) + " 年）");
        System.out.println();

        Map<String, Object> result = AverageCapitalPlusInterest.calculate(principal, yearRate, totalMonths);

        BigDecimal monthlyPayment = (BigDecimal) result.get("monthlyPayment");
        BigDecimal totalInterest = (BigDecimal) result.get("totalInterest");
        BigDecimal totalPayment = (BigDecimal) result.get("totalPayment");

        System.out.println("计算结果：");
        System.out.println("  每月固定月供：" + monthlyPayment + " 万元");
        System.out.println("  总利息：" + totalInterest + " 万元");
        System.out.println("  总还款额：" + totalPayment + " 万元");
        System.out.println();

        // 显示前 12 个月和最后 3 个月的明细
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> schedule = (List<Map<String, Object>>) result.get("schedule");
        System.out.println("分月还款明细（前 12 个月 + 最后 3 个月）：");
        System.out.println(String.format("%-5s %-12s %-12s %-12s %-15s", "月份", "月供(万)", "本金(万)", "利息(万)", "剩余本金(万)"));
        System.out.println("-".repeat(60));

        // 显示前 12 个月
        for (int i = 0; i < Math.min(12, schedule.size()); i++) {
            printMonthDetail(schedule.get(i));
        }

        if (schedule.size() > 15) {
            System.out.println("...");
        }

        // 显示最后 3 个月
        for (int i = Math.max(12, schedule.size() - 3); i < schedule.size(); i++) {
            printMonthDetail(schedule.get(i));
        }
    }

    /**
     * 测试等额本金计算
     */
    private static void testAverageCapital(BigDecimal principal, BigDecimal yearRate, int totalMonths) {
        System.out.println("【等额本金计算测试】");
        System.out.println("----------------------------");
        System.out.println("贷款本金：" + principal + " 万元");
        System.out.println("年利率：" + yearRate + " %");
        System.out.println("贷款月数：" + totalMonths + " 个月（" + (totalMonths / 12) + " 年）");
        System.out.println();

        Map<String, Object> result = AverageCapital.calculate(principal, yearRate, totalMonths);

        BigDecimal monthlyPrincipal = (BigDecimal) result.get("monthlyPrincipal");
        BigDecimal firstMonthPayment = (BigDecimal) result.get("firstMonthPayment");
        BigDecimal lastMonthPayment = (BigDecimal) result.get("lastMonthPayment");
        BigDecimal totalInterest = (BigDecimal) result.get("totalInterest");
        BigDecimal totalPayment = (BigDecimal) result.get("totalPayment");

        System.out.println("计算结果：");
        System.out.println("  每月固定本金：" + monthlyPrincipal + " 万元");
        System.out.println("  首月月供：" + firstMonthPayment + " 万元");
        System.out.println("  末月月供：" + lastMonthPayment + " 万元");
        System.out.println("  总利息：" + totalInterest + " 万元");
        System.out.println("  总还款额：" + totalPayment + " 万元");
        System.out.println();

        // 显示前 12 个月和最后 3 个月的明细
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> schedule = (List<Map<String, Object>>) result.get("schedule");
        System.out.println("分月还款明细（前 12 个月 + 最后 3 个月）：");
        System.out.println(String.format("%-5s %-12s %-12s %-12s %-15s", "月份", "月供(万)", "本金(万)", "利息(万)", "剩余本金(万)"));
        System.out.println("-".repeat(60));

        // 显示前 12 个月
        for (int i = 0; i < Math.min(12, schedule.size()); i++) {
            printMonthDetail(schedule.get(i));
        }

        if (schedule.size() > 15) {
            System.out.println("...");
        }

        // 显示最后 3 个月
        for (int i = Math.max(12, schedule.size() - 3); i < schedule.size(); i++) {
            printMonthDetail(schedule.get(i));
        }
    }

    /**
     * 打印单月还款明细
     */
    private static void printMonthDetail(Map<String, Object> monthDetail) {
        int month = (int) monthDetail.get("month");
        BigDecimal monthlyPayment = (BigDecimal) monthDetail.get("monthlyPayment");
        BigDecimal monthlyPrincipal = (BigDecimal) monthDetail.get("monthlyPrincipal");
        BigDecimal monthlyInterest = (BigDecimal) monthDetail.get("monthlyInterest");
        BigDecimal remainingPrincipal = (BigDecimal) monthDetail.get("remainingPrincipal");

        System.out.println(String.format("%-5d %-12s %-12s %-12s %-15s",
                month, monthlyPayment, monthlyPrincipal, monthlyInterest, remainingPrincipal));
    }
}

