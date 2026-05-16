package com.sang.javaexperiment.JavaProject4.ui;

import com.sang.javaexperiment.JavaProject4.entity.LoanRecord;
import com.sang.javaexperiment.JavaProject4.service.AverageCapital;
import com.sang.javaexperiment.JavaProject4.service.AverageCapitalPlusInterest;
import com.sang.javaexperiment.JavaProject4.service.LoanCalculatorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 购房贷款计算器 GUI 界面（集成数据库版本）
 * 提供输入、计算、结果展示和历史记录查看功能
 * 可通过 Spring 应用上下文获取 Service 实例
 */
public class LoanCalculatorUIWithDB extends JFrame {

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;

    private JTextField principalField;
    private JTextField yearRateField;
    private JTextField totalMonthsField;
    private JComboBox<String> loanTypeCombo;
    private JTextArea resultArea;
    private JButton calculateButton;
    private JButton historyButton;

    private final LoanCalculatorService loanCalculatorService;

    public LoanCalculatorUIWithDB(LoanCalculatorService loanCalculatorService) {
        this.loanCalculatorService = loanCalculatorService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("购房贷款计算器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);

        // 主容器
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 上部：输入面板
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // 中部：结果展示面板
        JPanel resultPanel = createResultPanel();
        mainPanel.add(resultPanel, BorderLayout.CENTER);

        // 下部：按钮面板
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * 创建输入面板
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("贷款信息输入"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 本金标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("贷款本金（万元）："), gbc);
        principalField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(principalField, gbc);

        // 年利率标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("年利率（%）："), gbc);
        yearRateField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(yearRateField, gbc);

        // 贷款月数标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("贷款月数："), gbc);
        totalMonthsField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(totalMonthsField, gbc);

        // 还款方式标签和下拉框
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("还款方式："), gbc);
        loanTypeCombo = new JComboBox<>(new String[]{"等额本息", "等额本金"});
        gbc.gridx = 1;
        panel.add(loanTypeCombo, gbc);

        return panel;
    }

    /**
     * 创建结果展示面板
     */
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("计算结果"));

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        resultArea.setLineWrap(false);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 创建按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        calculateButton = new JButton("计算");
        calculateButton.setPreferredSize(new Dimension(100, 40));
        calculateButton.addActionListener(this::handleCalculate);

        historyButton = new JButton("查看历史");
        historyButton.setPreferredSize(new Dimension(100, 40));
        historyButton.addActionListener(this::handleViewHistory);

        panel.add(calculateButton);
        panel.add(historyButton);

        JButton testConnectionButton = new JButton("测试连接");
        testConnectionButton.setPreferredSize(new Dimension(100, 40));
        testConnectionButton.addActionListener(e -> handleTestConnection());
        panel.add(testConnectionButton);

        return panel;
    }

    /**
     * 处理计算按钮点击事件
     */
    private void handleCalculate(ActionEvent e) {
        try {
            // 获取输入数据
            BigDecimal principal = new BigDecimal(principalField.getText().trim());
            BigDecimal yearRate = new BigDecimal(yearRateField.getText().trim());
            int totalMonths = Integer.parseInt(totalMonthsField.getText().trim());
            String loanType = (String) loanTypeCombo.getSelectedItem();

            // 验证输入
            if (principal.compareTo(BigDecimal.ZERO) <= 0 || yearRate.compareTo(BigDecimal.ZERO) <= 0 || totalMonths <= 0) {
                JOptionPane.showMessageDialog(this, "请输入有效的贷款参数（均需大于0）", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 执行计算并保存到数据库
            Map<String, Object> result;
            if (loanCalculatorService != null) {
                // 使用 Service 层执行计算并保存
                try {
                    result = loanCalculatorService.calculateAndSave(principal, yearRate, totalMonths, loanType);
                    JOptionPane.showMessageDialog(this, "✓ 计算结果已保存到数据库", "成功", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("[DEBUG] 计算结果已保存，Service 状态：可用");
                } catch (Exception dbEx) {
                    System.err.println("[ERROR] 数据库保存失败: " + dbEx.getMessage());
                    dbEx.printStackTrace();
                    // 仍然进行计算显示结果，但不保存到数据库
                    JOptionPane.showMessageDialog(this, "⚠ 计算成功但保存到数据库失败：" + dbEx.getMessage() + "\n\n原因可能：\n1. MySQL 未启动\n2. 数据库连接配置错误\n3. 表不存在", "警告", JOptionPane.WARNING_MESSAGE);
                    if ("等额本息".equals(loanType)) {
                        result = AverageCapitalPlusInterest.calculate(principal, yearRate, totalMonths);
                    } else {
                        result = AverageCapital.calculate(principal, yearRate, totalMonths);
                    }
                }
            } else {
                // 回退：仅本地计算（Service 未注入时）
                System.out.println("[DEBUG] Service 未注入，使用本地计算");
                JOptionPane.showMessageDialog(this, "⚠ Service 未初始化，仅进行本地计算（不保存到数据库）", "提示", JOptionPane.WARNING_MESSAGE);
                if ("等额本息".equals(loanType)) {
                    result = AverageCapitalPlusInterest.calculate(principal, yearRate, totalMonths);
                } else {
                    result = AverageCapital.calculate(principal, yearRate, totalMonths);
                }
            }

            // 展示结果
            displayResult(result, principal, yearRate, totalMonths, loanType);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "输入格式错误，请检查数值类型", "输入错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "计算过程出错：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 展示计算结果
     */
    private void displayResult(Map<String, Object> result, BigDecimal principal, BigDecimal yearRate, int totalMonths, String loanType) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== 贷款计算结果 ==========\n\n");
        sb.append("贷款基本信息：\n");
        sb.append("  贷款本金：").append(principal).append(" 万元\n");
        sb.append("  年利率：").append(yearRate).append(" %\n");
        sb.append("  贷款期限：").append(totalMonths).append(" 个月\n");
        sb.append("  还款方式：").append(loanType).append("\n\n");

        if ("等额本息".equals(loanType)) {
            BigDecimal monthlyPayment = (BigDecimal) result.get("monthlyPayment");
            sb.append("等额本息还款计划：\n");
            sb.append("  每月固定月供：").append(monthlyPayment).append(" 万元\n");
        } else {
            BigDecimal monthlyPrincipal = (BigDecimal) result.get("monthlyPrincipal");
            BigDecimal firstMonthPayment = (BigDecimal) result.get("firstMonthPayment");
            BigDecimal lastMonthPayment = (BigDecimal) result.get("lastMonthPayment");
            sb.append("等额本金还款计划：\n");
            sb.append("  每月固定本金：").append(monthlyPrincipal).append(" 万元\n");
            sb.append("  首月月供：").append(firstMonthPayment).append(" 万元\n");
            sb.append("  末月月供：").append(lastMonthPayment).append(" 万元\n");
        }

        BigDecimal totalInterest = (BigDecimal) result.get("totalInterest");
        BigDecimal totalPayment = (BigDecimal) result.get("totalPayment");

        sb.append("\n总体计算结果：\n");
        sb.append("  总利息：").append(totalInterest).append(" 万元\n");
        sb.append("  总还款额：").append(totalPayment).append(" 万元\n");

        sb.append("\n========== 分月还款明细 ==========\n\n");
        sb.append(String.format("%-5s %-12s %-12s %-12s %-15s\n", "月份", "月供(万)", "本金(万)", "利息(万)", "剩余本金(万)"));
        sb.append("-".repeat(60)).append("\n");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> schedule = (List<Map<String, Object>>) result.get("schedule");
        for (Map<String, Object> monthDetail : schedule) {
            int month = (int) monthDetail.get("month");
            BigDecimal monthlyPayment = (BigDecimal) monthDetail.get("monthlyPayment");
            BigDecimal monthlyPrincipal = (BigDecimal) monthDetail.get("monthlyPrincipal");
            BigDecimal monthlyInterest = (BigDecimal) monthDetail.get("monthlyInterest");
            BigDecimal remainingPrincipal = (BigDecimal) monthDetail.get("remainingPrincipal");

            sb.append(String.format("%-5d %-12s %-12s %-12s %-15s\n",
                    month, monthlyPayment, monthlyPrincipal, monthlyInterest, remainingPrincipal));
        }

        resultArea.setText(sb.toString());
        resultArea.setCaretPosition(0);
    }


    /**
     * 处理测试连接按钮点击事件
     */
    private void handleTestConnection() {
        if (loanCalculatorService == null) {
            JOptionPane.showMessageDialog(this, "✗ Service 未初始化", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                System.out.println("[DEBUG] 开始测试数据库连接...");
                boolean connected = loanCalculatorService.testDatabaseConnection();
                if (connected) {
                    System.out.println("[DEBUG] 数据库连接成功！");
                    JOptionPane.showMessageDialog(this, "✓ 数据库连接成功！\n可以正常使用所有功能。", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    System.out.println("[ERROR] 数据库连接失败");
                    JOptionPane.showMessageDialog(this, "✗ 数据库连接失败！\n\n请检查：\n1. MySQL 是否已启动\n2. 数据库 loan_calculator 是否存在\n3. 表 loan_history 是否存在\n4. 连接配置是否正确", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                System.err.println("[ERROR] 连接测试异常: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "✗ 连接测试异常：\n" + ex.getClass().getSimpleName() + "\n" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    /**
     * 处理查看历史按钮点击事件
     */
    private void handleViewHistory(ActionEvent e) {
        if (loanCalculatorService == null) {
            System.out.println("[ERROR] Service 未注入");
            JOptionPane.showMessageDialog(this, "✗ 数据库服务未初始化\n\n可能原因：\n1. Spring 容器启动失败\n2. LoanCalculatorService 注入失败", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        showHistoryDialog();
    }

    /**
     * 显示历史记录对话框
     */
    private void showHistoryDialog() {
        JDialog historyDialog = new JDialog(this, "历史计算记录", true);
        historyDialog.setSize(900, 500);
        historyDialog.setLocationRelativeTo(this);

        // 创建表格
        String[] columnNames = {"ID", "本金(万)", "年利率(%)", "月数", "还款方式", "总利息(万)", "计算时间"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        try {
            // 从数据库读取历史记录
            System.out.println("[DEBUG] 开始查询历史记录...");
            List<LoanRecord> records = loanCalculatorService.getAllRecords();
            System.out.println("[DEBUG] 查询到 " + records.size() + " 条记录");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (LoanRecord record : records) {
                Object[] row = {
                        record.getId(),
                        record.getPrincipal(),
                        record.getYearRate(),
                        record.getTotalMonths(),
                        record.getLoanType(),
                        record.getTotalInterest(),
                        record.getCalcTime() != null ? record.getCalcTime().format(formatter) : ""
                };
                tableModel.addRow(row);
            }

            if (records.isEmpty()) {
                JOptionPane.showMessageDialog(historyDialog, "✓ 连接成功\n但暂无历史记录。请先进行一次计算。", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(historyDialog, "✓ 成功读取 " + records.size() + " 条历史记录", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            System.err.println("[ERROR] 读取历史记录失败: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            ex.printStackTrace();
            String errorMsg = "✗ 读取历史记录失败\n\n错误类型：" + ex.getClass().getSimpleName() +
                            "\n错误信息：" + ex.getMessage() +
                            "\n\n常见原因：\n" +
                            "1. MySQL 服务未启动 (localhost:3306)\n" +
                            "2. 数据库连接配置错误\n" +
                            "3. 数据库表不存在\n" +
                            "4. 权限不足";
            JOptionPane.showMessageDialog(historyDialog, errorMsg, "错误", JOptionPane.ERROR_MESSAGE);
        }

        JTable historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(30);

        JScrollPane scrollPane = new JScrollPane(historyTable);

        historyDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e1 -> historyDialog.dispose());
        buttonPanel.add(closeButton);

        historyDialog.add(buttonPanel, BorderLayout.SOUTH);
        historyDialog.setVisible(true);
    }
}

