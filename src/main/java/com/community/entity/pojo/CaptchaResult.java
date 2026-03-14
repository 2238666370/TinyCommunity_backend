package com.community.entity.pojo;

/**
 * ClassName: CaptchaResult
 * Package: com.community.entity.pojo
 * Description:
 *
 * @Author wth
 * @Create 2026/3/11 19:57
 * @Version 1.0
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 验证码结果封装类
 */
public class CaptchaResult {
    private BufferedImage image;
    private String code;
    private long timestamp;

    public CaptchaResult(BufferedImage image, String code) {
        this.image = image;
        this.code = code;
        this.timestamp = System.currentTimeMillis();
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getCode() {
        return code;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 将图片转换为Base64字符串
     *
     * @return Base64格式的图片字符串
     */
    public String toBase64() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        baos.close();
        return java.util.Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 将图片转换为字节数组
     *
     * @return 图片字节数组
     */
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }
}
