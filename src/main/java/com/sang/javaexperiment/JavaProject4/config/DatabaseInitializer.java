package com.sang.javaexperiment.JavaProject4.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 数据库初始化器
 * 在应用启动时自动创建数据库与历史记录表，避免“数据库未连接/表不存在”导致历史记录功能不可用。
 */
@Component
public class DatabaseInitializer {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    /**
     * 自动创建数据库与历史记录表。
     */
    public void ensureDatabaseAndTable() {
        String serverUrl = buildServerUrl(datasourceUrl);

        try (Connection connection = DriverManager.getConnection(serverUrl, username, password);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS loan_calculator CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            statement.executeUpdate("USE loan_calculator");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS loan_history (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "principal DECIMAL(15, 2) NOT NULL COMMENT '贷款本金（万元）', " +
                    "year_rate DECIMAL(5, 2) NOT NULL COMMENT '年利率（%）', " +
                    "total_months INT NOT NULL COMMENT '贷款月数', " +
                    "loan_type VARCHAR(20) NOT NULL COMMENT '还款方式（等额本息/等额本金）', " +
                    "total_interest DECIMAL(15, 2) NOT NULL COMMENT '总利息', " +
                    "calc_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间'" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

        } catch (SQLException ex) {
            throw new IllegalStateException("数据库初始化失败，请检查 MySQL 服务是否启动、账号密码是否正确，以及 root 用户是否有建库建表权限。", ex);
        }
    }

    private String buildServerUrl(String jdbcUrl) {
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:mysql://")) {
            throw new IllegalArgumentException("当前 datasource.url 不是有效的 MySQL JDBC 地址：" + jdbcUrl);
        }

        int queryIndex = jdbcUrl.indexOf('?');
        String withoutQuery = queryIndex >= 0 ? jdbcUrl.substring(0, queryIndex) : jdbcUrl;
        int lastSlashIndex = withoutQuery.lastIndexOf('/');
        if (lastSlashIndex < "jdbc:mysql://".length()) {
            throw new IllegalArgumentException("当前 datasource.url 格式不正确，无法解析数据库名：" + jdbcUrl);
        }

        String serverBase = withoutQuery.substring(0, lastSlashIndex + 1);
        return queryIndex >= 0 ? serverBase + jdbcUrl.substring(queryIndex) : serverBase;
    }
}

