package com.sang.javaexperiment.JavaProject1;

import lombok.Getter;

import java.time.LocalDate;

@Getter
/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 用于封装从身份证号中提取出的个人信息的简单 POJO。
 * 字段说明：
 * - idNumber: 原始身份证号（字符串形式，保留校验位）
 * - birthday: 出生日期（只含日期，不含时间）
 * - age: 根据出生日期计算得到的整数年龄
 * - sex: 中文性别描述，例如 "男" 或 "女"
 */
public class PersonalInfo {
    /**
     * 原始身份证号
     */
    private final String idNumber;

    /**
     * 出生日期
     */
    private final LocalDate birthday;

    /**
     * 年龄
     */
    private final Integer age;

    /**
     * 性别
     */
    private final String sex;

    /**
     * 构造函数
     *
     * @param idNumber 原始身份证号
     * @param birthday 出生日期
     * @param age      年龄（岁）
     * @param sex      中文性别
     */
    public PersonalInfo(String idNumber, LocalDate birthday, Integer age, String sex) {
        this.idNumber = idNumber;
        this.birthday = birthday;
        this.age = age;
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "PersonalInfo{" +
                "idNumber='" + idNumber + '\'' +
                ", birthday=" + birthday +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                '}';
    }
}
