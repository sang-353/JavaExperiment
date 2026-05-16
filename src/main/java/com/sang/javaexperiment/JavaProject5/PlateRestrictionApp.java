package com.sang.javaexperiment.JavaProject5;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * 实验五：车牌单双号限行出行服务
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 规则：若 (当日日期 % 2) == (车牌最后一个数字 % 2)，则可通行；否则判定违规。
 */
public class PlateRestrictionApp {

    private static final int PENALTY_POINTS = 3;
    private static final int PENALTY_FINE = 200;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("请输入车牌号：");
            if (!scanner.hasNextLine()) {
                System.out.println("车牌号无效：输入被截断。请重新输入。");
                return;
            }

            String plateNumber = scanner.nextLine();
            if (plateNumber == null || plateNumber.trim().isEmpty()) {
                System.out.println("车牌号无效：输入不能为空。");
                return;
            }

            Integer lastDigit = extractLastDigit(plateNumber);
            if (lastDigit == null) {
                System.out.println("车牌号无效：未找到数字尾号。");
                return;
            }

            int dayOfMonth = LocalDate.now().getDayOfMonth();
            boolean allowed = isTravelAllowed(dayOfMonth, lastDigit);

            System.out.println("今日日期（日）：" + dayOfMonth);
            System.out.println("车牌最后数字：" + lastDigit);

            if (allowed) {
                System.out.println("判定结果：尾号与日期奇偶匹配，准予通行。");
            } else {
                System.out.println("判定结果：尾号与日期奇偶不匹配，属于限行违规。");
                System.out.println("处罚结果：扣" + PENALTY_POINTS + "分，罚款" + PENALTY_FINE + "元。");
            }
        }
    }

    static Integer extractLastDigit(String plateNumber) {
        for (int index = plateNumber.length() - 1; index >= 0; index--) {
            char current = plateNumber.charAt(index);
            if (Character.isDigit(current)) {
                return current - '0';
            }
        }
        return null;
    }

    static boolean isTravelAllowed(int dayOfMonth, int plateLastDigit) {
        // 日期与车牌尾号的奇偶性必须相同
        boolean dayIsEven = dayOfMonth % 2 == 0;
        boolean digitIsEven = plateLastDigit % 2 == 0;
        return dayIsEven == digitIsEven;
    }
}

