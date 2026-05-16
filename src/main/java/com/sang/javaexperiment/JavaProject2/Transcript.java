package com.sang.javaexperiment.JavaProject2;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map.Entry;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * Transcript 类：用于录入多门课程成绩、验证、降序排序并计算平均分。
 * 使用说明（交互式）：
 * 1. 运行程序后，先输入科目数（正整数）。
 * 2. 依次输入每门课程的名称（单个词或不含空格的标识）和成绩（整数）。
 * 3. 程序会忽略不在 0-100 范围内的成绩（视为无效），并只对有效成绩进行排序与平均分计算。
 */
public class Transcript {

	// 成员变量：科目数
	private int courseNumber;
	// 使用 HashMap 存储 <课程名, 成绩>，只保存有效成绩（0-100）
	private Map<String, Integer> scoreMap;

	/**
	 * 入口方法：执行一次完整的录入—处理—输出流程
	 */
	public void run() {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.print("请输入科目数：");
			if (!scanner.hasNextInt()) {
				System.out.println("输入的科目数不是有效的整数，程序退出。");
				return;
			}
			courseNumber = scanner.nextInt();
			if (courseNumber <= 0) {
				System.out.println("科目数必须为正整数，程序退出。");
				return;
			}

			// 使用 HashMap 存储有效成绩
			scoreMap = new HashMap<>();

			// 录入每门课程的名称与成绩，遇到无效成绩则忽略（不放入 map）
			for (int i = 0; i < courseNumber; i++) {
				System.out.print("请输入第" + (i + 1) + "门课程名称（不含空格）：");
				String name = scanner.next();
				System.out.print("请输入该课程的成绩（整数，0-100）：");
				if (!scanner.hasNextInt()) {
					// 非整数输入视为无效成绩，跳过并吞掉输入
					System.out.println("成绩输入不是整数，已忽略该成绩。");
					scanner.next();
					continue;
				}
				int score = scanner.nextInt();
				// 验证是否为百分制 0-100
				if (score < 0 || score > 100) {
					System.out.println("成绩不在 0-100 范围内，已忽略该成绩。");
				} else {
					// 只有有效成绩才放入 map
					scoreMap.put(name, score);
				}
			}

			// 使用 HashMap 的数据进行排序和输出
			processAndPrintResults(scoreMap);
		}
	}

	/**
	 * 处理成绩：筛选有效项、按成绩降序排列、计算平均分并输出结果
	 */
	private void processAndPrintResults(Map<String, Integer> map) {
		if (map == null || map.isEmpty()) {
			System.out.println("没有有效成绩，无法排序和计算平均分。");
			return;
		}

		// 计算总分与平均分
		int sum = 0;
		for (int v : map.values()) sum += v;
		int validCount = map.size();
		double average = (double) sum / validCount;

		// 将 map 的 entry 转为 list，然后按 value 降序排序
		List<Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
		entries.sort(Comparator.comparing(Entry<String, Integer>::getValue).reversed());

		// 输出排序结果
		System.out.print("各科成绩（从高到低）：");
		for (int i = 0; i < entries.size(); i++) {
			Entry<String, Integer> e = entries.get(i);
			System.out.print(e.getKey() + " " + e.getValue());
			if (i < entries.size() - 1) System.out.print("  ");
		}
		System.out.println();
		System.out.printf("平均分：%.1f\n", average);
	}

	/**
	 * 程序入口，直接运行 Transcript 可完成交互式输入与输出
	 */
	public static void main(String[] args) {
		new Transcript().run();
	}
}
