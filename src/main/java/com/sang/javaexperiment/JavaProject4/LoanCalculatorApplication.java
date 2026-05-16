package com.sang.javaexperiment.JavaProject4;

import com.sang.javaexperiment.JavaProject4.config.DatabaseInitializer;
import com.sang.javaexperiment.JavaProject4.service.LoanCalculatorService;
import com.sang.javaexperiment.JavaProject4.ui.LoanCalculatorUIWithDB;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.swing.*;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 购房贷款计算器应用启动类
 * 基于 Spring Boot 框架，启动 Swing GUI 应用
 */
@SpringBootApplication
@MapperScan("com.sang.javaexperiment.JavaProject4.mapper")
public class LoanCalculatorApplication {

    private static final String SANITIZED_DATASOURCE_URL =
            "jdbc:mysql://localhost:3306/loan_calculator?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";

    public static void main(String[] args) {
        // 明确开启 AWT 图形模式，避免 HeadlessException
        System.setProperty("java.awt.headless", "false");
        System.setProperty("spring.datasource.url", SANITIZED_DATASOURCE_URL);

        SpringApplication application = new SpringApplication(LoanCalculatorApplication.class);
        application.setHeadless(false);
        application.run(args);
    }

    @Bean
    public ApplicationRunner showMainWindow(LoanCalculatorService loanCalculatorService,
                                            DatabaseInitializer databaseInitializer) {
        return args -> {
            databaseInitializer.ensureDatabaseAndTable();
            SwingUtilities.invokeLater(() -> {
                LoanCalculatorUIWithDB frame = new LoanCalculatorUIWithDB(loanCalculatorService);
                frame.setVisible(true);
            });
        };
    }
}

