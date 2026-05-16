package com.sang.javaexperiment.JavaProject4.ui;

import com.sang.javaexperiment.JavaProject4.entity.LoanRecord;
import com.sang.javaexperiment.JavaProject4.service.AverageCapital;
import com.sang.javaexperiment.JavaProject4.service.AverageCapitalPlusInterest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 购房贷款计算器 GUI 界面
 * 提供输入、计算、结果展示和历史记录查看功能
 */
public class LoanCalculatorUI extends JFrame {

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;

    private JTextField principalField;
    private JTextField yearRateField;
    private JTextField totalMonthsField;
    private JComboBox<String> loanTypeCombo;
    private JTextArea resultArea;
    private JButton calculateButton;
    private JButton historyButton;

    // 用于保存当前计算的数据，以便查看历史时使用
    private List<LoanRecord> historyRecords;

    public LoanCalculatorUI() {
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

            // 执行计算（不连接数据库的本地计算）
            Map<String, Object> result;
            if ("等额本息".equals(loanType)) {
                result = AverageCapitalPlusInterest.calculate(principal, yearRate, totalMonths);
            } else {
                result = AverageCapital.calculate(principal, yearRate, totalMonths);
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
     * 处理查看历史按钮点击事件
     */
    private void handleViewHistory(ActionEvent e) {
        // 模拟从数据库读取历史记录（实际应通过服务层调用）
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

        // 示例数据（实际应从数据库读取）
        Object[][] exampleData = {
                {1, "100.00", "4.90", 360, "等额本息", "54.32", "2024-05-13 14:30:00"},
                {2, "50.00", "3.85", 240, "等额本金", "18.50", "2024-05-12 10:15:00"}
        };

        for (Object[] row : exampleData) {
            tableModel.addRow(row);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoanCalculatorUI frame = new LoanCalculatorUI();
            frame.setVisible(true);
        });
    }
}

