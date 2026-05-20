package com.sang.javaexperiment.JavaProject1;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 */
public class PrintInfo {

	/**
	 * 匹配 18 位中国身份证号的正则：前 17 位为数字，第 18 位可以是数字或大写/小写 X
	 */
	private static final Pattern ID_NUMBER_PATTERN = Pattern.compile("^\\d{17}[\\dXx]$");

	/**
	 * 用于解析身份证中出生日期部分（格式为 yyyyMMdd）的解析器
	 */
	private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

	public static void main(String[] args) {
		String idNumber = args.length > 0 ? args[0] : readIdNumberFromConsole();
		if (idNumber == null || idNumber.isBlank()) {
			System.out.println("请输入18位身份证号码：");
			return;
		}

		try {
			// 解析身份证，返回封装好的 PersonalInfo 对象
			PersonalInfo info = extractPersonalInfo(idNumber);
			System.out.println(toDisplayString(info));
		} catch (IllegalArgumentException exception) {
			System.out.println("身份证号码无效：" + exception.getMessage());
		}
	}

	public static PersonalInfo extractPersonalInfo(String idNumber) {
		String normalizedId = normalizeIdNumber(idNumber);

		if (!ID_NUMBER_PATTERN.matcher(normalizedId).matches()) {
			throw new IllegalArgumentException("身份证号必须是18位，前17位为数字，最后一位为数字或X。");
		}

		LocalDate birthDate;
		try {
			birthDate = LocalDate.parse(normalizedId.substring(6, 14), BIRTH_DATE_FORMATTER);
		} catch (DateTimeParseException exception) {
			throw new IllegalArgumentException("身份证号中的出生日期不合法。", exception);
		}

		LocalDate today = LocalDate.now();
		if (birthDate.isAfter(today)) {
			throw new IllegalArgumentException("身份证号中的出生日期不能晚于当前日期。");
		}

		// 计算年龄（按年数通过 Period 计算）
		int age = Period.between(birthDate, today).getYears();
		// 根据第17位数字判断性别：奇数为男，偶数为女
		String sex = determineGender(normalizedId.charAt(16));

		// 使用统一的 PersonalInfo 对象封装结果并返回
		return new PersonalInfo(normalizedId, birthDate, age, sex);
	}

	private static String normalizeIdNumber(String idNumber) {
		// 简单校验并去掉首尾空白字符
		if (idNumber == null) {
			throw new IllegalArgumentException("身份证号不能为空。");
		}
		return idNumber.trim();
	}

	private static String determineGender(char genderCode) {
		// genderCode 应该是身份证第17位（从 0 开始索引为 16），必须为数字
		if (!Character.isDigit(genderCode)) {
			throw new IllegalArgumentException("身份证号第17位必须是数字。");
		}
		// 根据奇偶性返回性别
		return ((genderCode - '0') % 2 == 0) ? "女" : "男";
	}

	private static String readIdNumberFromConsole() {
		System.out.print("请输入18位身份证号码：");
		try (Scanner scanner = new Scanner(System.in)) {
			return scanner.hasNextLine() ? scanner.nextLine() : null;
		}
	}

	private static String toDisplayString(PersonalInfo info) {
		// 将解析结果格式化为自我介绍句子
		return "大家好，我的名字叫sang，今年" + info.getAge() + "岁，性别：" + info.getSex()
				+ "，目前就读于中南民族大学计算机学院计算机科学与技术专业，\n"+"主要技能是：C、C++、Java、Python、后端开发、数据库技术等，"
				+ "兴趣爱好是：web开发、篮球、游戏、旅游。";
	}
}
