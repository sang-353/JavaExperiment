package com.sang.javaexperiment.JavaProject3;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 实验3：小C的字符画表白卡
 * 功能：读取图片并转换为 ASCII 字符画，结合实时日期和表白文案展示在 Swing 窗口中。
 */
public class LoveCard extends JFrame {

    private static final String ASCII_CHARS = "@#8&o:*. ";
    private static final int DEFAULT_OUTPUT_WIDTH = 150;
    private static final double HEIGHT_COMPENSATION = 0.5;
    private static final String TOP_CONFESSION = "小美，我喜欢你很久了，你可以做我女朋友吗？";
    private static final String FOOTER_SIGNATURE = "爱你的小帅！";

    private final JTextArea resultArea;
    private final JButton generateButton;

    public LoveCard() {
        setTitle("小C的字符画表白卡生成器");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        generateButton = new JButton("点击生成表白卡");
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        // 等宽字体是字符画不变形的关键
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 8));

        JScrollPane scrollPane = new JScrollPane(resultArea);

        generateButton.addActionListener(event -> processImage());

        add(generateButton, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void processImage() {
        File imageFile = chooseImageFile();
        if (imageFile == null) {
            return;
        }

        try {
            BufferedImage sourceImage = ImageIO.read(imageFile);
            if (sourceImage == null) {
                JOptionPane.showMessageDialog(this, "图片格式无法识别，请重新选择。", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String cardText = buildLoveCardText(sourceImage, "To My Dearest", FOOTER_SIGNATURE);
            resultArea.setText(cardText);
            resultArea.setCaretPosition(0);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "读取图片失败：" + exception.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private File chooseImageFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("请选择用于生成字符画的图片");
        chooser.setFileFilter(new FileNameExtensionFilter("图片文件 (*.jpg, *.jpeg, *.png, *.gif)", "jpg", "jpeg", "png", "gif"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    private String buildLoveCardText(BufferedImage image, String message, String signature) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));

        StringBuilder builder = new StringBuilder();
        builder.append("      日期：").append(dateStr).append('\n');
        builder.append("      ").append(TOP_CONFESSION).append('\n');
        builder.append("      ").append(message).append("\n\n");

        BufferedImage scaledImage = scaleImage(image, DEFAULT_OUTPUT_WIDTH);
        char[][] asciiCanvas = new char[scaledImage.getHeight()][scaledImage.getWidth()];
        for (int y = 0; y < scaledImage.getHeight(); y++) {
            for (int x = 0; x < scaledImage.getWidth(); x++) {
                asciiCanvas[y][x] = mapPixelToAscii(scaledImage.getRGB(x, y));
            }
        }

        for (char[] row : asciiCanvas) {
            builder.append(row);
            builder.append('\n');
        }

        builder.append('\n');
        builder.append(centerText(signature, scaledImage.getWidth()));
        builder.append('\n');

        return builder.toString();
    }

    private char mapPixelToAscii(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        double gray = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        int index = (int) Math.round(gray * (ASCII_CHARS.length() - 1) / 255.0);
        return ASCII_CHARS.charAt(index);
    }

    private String centerText(String text, int width) {
        if (text == null || text.isBlank()) {
            return "";
        }
        if (text.length() >= width) {
            return text;
        }

        int leftPadding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, leftPadding)) + text;
    }

    private BufferedImage scaleImage(BufferedImage sourceImage, int targetWidth) {
        int safeWidth = Math.max(targetWidth, 1);
        int targetHeight = Math.max((int) (sourceImage.getHeight() * ((double) safeWidth / sourceImage.getWidth()) * HEIGHT_COMPENSATION), 1);

        Image tmp = sourceImage.getScaledInstance(safeWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage scaled = new BufferedImage(safeWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = scaled.createGraphics();
        graphics.drawImage(tmp, 0, 0, null);
        graphics.dispose();

        return scaled;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoveCard().setVisible(true));
    }
}

