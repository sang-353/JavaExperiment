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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

    private static final String ASCII_CHARS = "@%#*+=-:. ";
    private static final int MIN_OUTPUT_WIDTH = 120;
    private static final int MAX_OUTPUT_WIDTH = 180;
    private static final double PORTRAIT_HEIGHT_COMPENSATION = 0.44;
    private static final double LANDSCAPE_HEIGHT_COMPENSATION = 0.50;
    private static final double CONTRAST_LOW_CLIP = 0.02;
    private static final double CONTRAST_HIGH_CLIP = 0.98;
    private static final double GAMMA = 0.92;
    private static final double DETAIL_ENHANCEMENT = 0.62;
    private static final String TOP_CONFESSION = "小美，我喜欢你很久了，你可以做我女朋友吗？";
    private static final String FOOTER_SIGNATURE = "爱你的小帅！";

    private final JTextArea resultArea;

    public LoveCard() {
        setTitle("小C的字符画表白卡生成器");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton generateButton = new JButton("点击生成表白卡");
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

            String cardText = buildLoveCardText(sourceImage);
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

    private String buildLoveCardText(BufferedImage image) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));

        StringBuilder builder = new StringBuilder();
        builder.append("      日期：").append(dateStr).append('\n');
        builder.append("      ").append(TOP_CONFESSION).append('\n');
        builder.append("      To My Dearest\n\n");

        double[][] adjustedLuminance = prepareLuminance(image);
        char[][] asciiCanvas = renderAsciiCanvas(adjustedLuminance);
        for (char[] row : asciiCanvas) {
            builder.append(row);
            builder.append('\n');
        }

        builder.append('\n');
        builder.append(centerSignatureLine(asciiCanvas[0].length));
        builder.append('\n');

        return builder.toString();
    }

    private double[][] prepareLuminance(BufferedImage sourceImage) {
        int targetWidth = chooseOutputWidth(sourceImage);
        BufferedImage scaledImage = scaleImage(sourceImage, targetWidth);

        double[][] luminance = extractLuminance(scaledImage);
        stretchContrast(luminance);
        enhanceLocalDetail(luminance);
        applyGamma(luminance);

        return luminance;
    }

    private char[][] renderAsciiCanvas(double[][] luminance) {
        int height = luminance.length;
        int width = luminance[0].length;
        char[][] canvas = new char[height][width];
        double[][] errorBuffer = copyMatrix(luminance);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double current = clamp255(errorBuffer[y][x]);
                int index = luminanceToIndex(current);
                char chosen = ASCII_CHARS.charAt(index);
                canvas[y][x] = chosen;

                double target = indexToLuminance(index);
                double error = current - target;
                if (x + 1 < width) {
                    errorBuffer[y][x + 1] += error * 7.0 / 16.0;
                }
                if (y + 1 < height) {
                    if (x > 0) {
                        errorBuffer[y + 1][x - 1] += error * 3.0 / 16.0;
                    }
                    errorBuffer[y + 1][x] += error * 5.0 / 16.0;
                    if (x + 1 < width) {
                        errorBuffer[y + 1][x + 1] += error / 16.0;
                    }
                }
            }
        }

        return canvas;
    }

    private String centerSignatureLine(int width) {
        if (FOOTER_SIGNATURE.length() >= width) {
            return FOOTER_SIGNATURE;
        }

        int leftPadding = (width - FOOTER_SIGNATURE.length()) / 2;
        if (leftPadding <= 0) {
            return FOOTER_SIGNATURE;
        }
        return " ".repeat(leftPadding) + FOOTER_SIGNATURE;
    }

    private BufferedImage scaleImage(BufferedImage sourceImage, int targetWidth) {
        int safeWidth = Math.max(targetWidth, 1);
        double compensation = chooseHeightCompensation(sourceImage);
        int targetHeight = Math.max((int) Math.round(sourceImage.getHeight() * ((double) safeWidth / sourceImage.getWidth()) * compensation), 1);

        BufferedImage scaled = new BufferedImage(safeWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = scaled.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(sourceImage, 0, 0, safeWidth, targetHeight, null);
        graphics.dispose();

        return scaled;
    }

    private int chooseOutputWidth(BufferedImage image) {
        double aspectRatio = (double) image.getWidth() / image.getHeight();
        if (aspectRatio < 0.9) {
            return clampOutputWidth((int) Math.round(image.getWidth() * 0.78));
        } else if (aspectRatio < 1.2) {
            return clampOutputWidth((int) Math.round(image.getWidth() * 0.72));
        } else if (aspectRatio < 1.6) {
            return clampOutputWidth((int) Math.round(image.getWidth() * 0.66));
        } else {
            return clampOutputWidth((int) Math.round(image.getWidth() * 0.60));
        }
    }

    private int clampOutputWidth(int targetWidth) {
        return Math.max(MIN_OUTPUT_WIDTH, Math.min(MAX_OUTPUT_WIDTH, targetWidth));
    }

    private double chooseHeightCompensation(BufferedImage image) {
        double aspectRatio = (double) image.getWidth() / image.getHeight();
        if (aspectRatio < 1.0) {
            return PORTRAIT_HEIGHT_COMPENSATION;
        }
        if (aspectRatio < 1.4) {
            return 0.47;
        }
        return LANDSCAPE_HEIGHT_COMPENSATION;
    }

    private double[][] extractLuminance(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] luminance = new double[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                luminance[y][x] = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
            }
        }

        return luminance;
    }

    private void stretchContrast(double[][] luminance) {
        int[] histogram = new int[256];
        for (double[] row : luminance) {
            for (double value : row) {
                histogram[(int) clamp255(value)]++;
            }
        }

        int total = luminance.length * luminance[0].length;
        int lowThreshold = (int) Math.round(total * CONTRAST_LOW_CLIP);
        int highThreshold = (int) Math.round(total * CONTRAST_HIGH_CLIP);

        int accumulated = 0;
        int low = 0;
        for (int i = 0; i < histogram.length; i++) {
            accumulated += histogram[i];
            if (accumulated >= lowThreshold) {
                low = i;
                break;
            }
        }

        accumulated = 0;
        int high = histogram.length - 1;
        for (int i = 0; i < histogram.length; i++) {
            accumulated += histogram[i];
            if (accumulated >= highThreshold) {
                high = i;
                break;
            }
        }

        if (high <= low) {
            return;
        }

        double scale = 255.0 / (high - low);
        for (int y = 0; y < luminance.length; y++) {
            for (int x = 0; x < luminance[y].length; x++) {
                luminance[y][x] = clamp255((luminance[y][x] - low) * scale);
            }
        }
    }

    private void applyGamma(double[][] luminance) {

        for (int y = 0; y < luminance.length; y++) {
            for (int x = 0; x < luminance[y].length; x++) {
                luminance[y][x] = 255.0 * Math.pow(clamp255(luminance[y][x]) / 255.0, GAMMA);
            }
        }
    }

    private void enhanceLocalDetail(double[][] luminance) {
        double[][] blur = boxBlur(luminance);
        for (int y = 0; y < luminance.length; y++) {
            for (int x = 0; x < luminance[y].length; x++) {
                double detail = luminance[y][x] - blur[y][x];
                luminance[y][x] = clamp255(luminance[y][x] + detail * DETAIL_ENHANCEMENT);
            }
        }
    }

    private double[][] boxBlur(double[][] source) {
        int height = source.length;
        int width = source[0].length;
        double[][] blurred = new double[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sum = 0;
                int count = 0;
                for (int dy = -1; dy <= 1; dy++) {
                    int ny = y + dy;
                    if (ny < 0 || ny >= height) {
                        continue;
                    }
                    for (int dx = -1; dx <= 1; dx++) {
                        int nx = x + dx;
                        if (nx < 0 || nx >= width) {
                            continue;
                        }
                        sum += source[ny][nx];
                        count++;
                    }
                }
                blurred[y][x] = sum / Math.max(count, 1);
            }
        }

        return blurred;
    }

    private double[][] copyMatrix(double[][] source) {
        double[][] copy = new double[source.length][source[0].length];
        for (int y = 0; y < source.length; y++) {
            System.arraycopy(source[y], 0, copy[y], 0, source[y].length);
        }
        return copy;
    }

    private int luminanceToIndex(double luminance) {
        int index = (int) Math.round(clamp255(luminance) * (ASCII_CHARS.length() - 1) / 255.0);
        return Math.max(0, Math.min(ASCII_CHARS.length() - 1, index));
    }

    private double indexToLuminance(int index) {
        return index * 255.0 / (ASCII_CHARS.length() - 1);
    }

    private double clamp255(double value) {
        return Math.max(0.0, Math.min(255.0, value));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoveCard().setVisible(true));
    }
}

