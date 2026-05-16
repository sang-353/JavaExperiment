package com.sang.javaexperiment.JavaProject4.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LoanCalculatorAlgorithmsTest {

    @Test
    void averageCapitalPlusInterest_shouldGenerateFullScheduleAndClearPrincipal() {
        Map<String, Object> result = AverageCapitalPlusInterest.calculate(new BigDecimal("100.00"), new BigDecimal("4.90"), 360);

        assertNotNull(result.get("monthlyPayment"));
        assertNotNull(result.get("totalInterest"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> schedule = (List<Map<String, Object>>) result.get("schedule");
        assertEquals(360, schedule.size());

        Map<String, Object> firstMonth = schedule.get(0);
        Map<String, Object> lastMonth = schedule.get(schedule.size() - 1);

        BigDecimal firstInterest = (BigDecimal) firstMonth.get("monthlyInterest");
        BigDecimal lastRemaining = (BigDecimal) lastMonth.get("remainingPrincipal");

        assertTrue(firstInterest.compareTo(BigDecimal.ZERO) > 0);
        assertEquals(0, lastRemaining.compareTo(new BigDecimal("0.00")));
    }

    @Test
    void averageCapital_shouldDecreasePaymentAndClearPrincipal() {
        Map<String, Object> result = AverageCapital.calculate(new BigDecimal("100.00"), new BigDecimal("4.90"), 360);

        BigDecimal firstMonthPayment = (BigDecimal) result.get("firstMonthPayment");
        BigDecimal lastMonthPayment = (BigDecimal) result.get("lastMonthPayment");
        assertTrue(firstMonthPayment.compareTo(lastMonthPayment) > 0);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> schedule = (List<Map<String, Object>>) result.get("schedule");
        assertEquals(360, schedule.size());

        Map<String, Object> firstMonth = schedule.get(0);
        Map<String, Object> lastMonth = schedule.get(schedule.size() - 1);

        BigDecimal firstInterest = (BigDecimal) firstMonth.get("monthlyInterest");
        BigDecimal lastInterest = (BigDecimal) lastMonth.get("monthlyInterest");
        BigDecimal lastRemaining = (BigDecimal) lastMonth.get("remainingPrincipal");

        assertTrue(firstInterest.compareTo(lastInterest) > 0);
        assertEquals(0, lastRemaining.compareTo(new BigDecimal("0.00")));
    }

    @Test
    void bothMethods_shouldSupportShortTerms() {
        Map<String, Object> acpi = AverageCapitalPlusInterest.calculate(new BigDecimal("30.00"), new BigDecimal("3.20"), 12);
        Map<String, Object> ac = AverageCapital.calculate(new BigDecimal("30.00"), new BigDecimal("3.20"), 12);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> acpiSchedule = (List<Map<String, Object>>) acpi.get("schedule");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> acSchedule = (List<Map<String, Object>>) ac.get("schedule");

        assertEquals(12, acpiSchedule.size());
        assertEquals(12, acSchedule.size());
    }
}

