package com.community.util;
import com.community.entity.pojo.CaptchaResult;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * 图形验证码生成工具类
 */

public class CaptchaGenerator {

    // 默认配置
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_HEIGHT = 40;
    private static final int DEFAULT_CODE_COUNT = 4;
    private static final int DEFAULT_LINE_COUNT = 20;
    private static final int DEFAULT_FONT_SIZE = 30;

    // 可选字符（去掉了容易混淆的字符）
    private static final char[] CODE_SEQUENCE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    /**
     * 生成验证码图片和对应的验证码字符串
     *
     * @return CaptchaResult 包含验证码图片和验证码字符串
     */
    public static CaptchaResult generate() {
        return generate(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_CODE_COUNT, DEFAULT_LINE_COUNT);
    }

    /**
     * 生成验证码图片和对应的验证码字符串（可自定义参数）
     *
     * @param width 图片宽度
     * @param height 图片高度
     * @param codeCount 验证码字符个数
     * @param lineCount 干扰线数量
     * @return CaptchaResult 包含验证码图片和验证码字符串
     */
    public static CaptchaResult generate(int width, int height, int codeCount, int lineCount) {
        // 创建内存中的图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 获取图形上下文
        Graphics2D graphics = image.createGraphics();

        // 设置背景色
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        // 设置字体
        graphics.setFont(new Font("Arial", Font.BOLD, DEFAULT_FONT_SIZE));

        // 创建随机数生成器
        Random random = new Random();

        // 绘制干扰线
        for (int i = 0; i < lineCount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            graphics.setColor(getRandomColor(150, 200));
            graphics.drawLine(x, y, x + xl, y + yl);
        }

        // 生成随机验证码
        StringBuilder captchaCode = new StringBuilder();
        int codeX = width / (codeCount + 1);

        for (int i = 0; i < codeCount; i++) {
            String code = String.valueOf(CODE_SEQUENCE[random.nextInt(CODE_SEQUENCE.length)]);
            captchaCode.append(code);

            // 设置字符颜色
            graphics.setColor(getRandomColor(20, 120));

            // 添加字符旋转效果
            int rotateAngle = random.nextInt(30) - 15;
            graphics.rotate(Math.toRadians(rotateAngle), codeX * (i + 1), height / 2);

            // 绘制字符
            graphics.drawString(code, codeX * (i + 1) - DEFAULT_FONT_SIZE / 2, height / 2 + DEFAULT_FONT_SIZE / 3);

            // 恢复旋转
            graphics.rotate(-Math.toRadians(rotateAngle), codeX * (i + 1), height / 2);
        }

        // 释放图形上下文
        graphics.dispose();

        return new CaptchaResult(image, captchaCode.toString());
    }

    /**
     * 获取随机颜色
     *
     * @param min 最小颜色值
     * @param max 最大颜色值
     * @return 随机颜色
     */
    private static Color getRandomColor(int min, int max) {
        Random random = new Random();
        int r = min + random.nextInt(max - min);
        int g = min + random.nextInt(max - min);
        int b = min + random.nextInt(max - min);
        return new Color(r, g, b);
    }

}