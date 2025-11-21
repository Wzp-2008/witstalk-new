package top.xinsin.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES加密工具类
 * 支持ECB和CBC模式，提供加密、解密、密钥生成等功能
 */
public class AESUtils {

    // 算法名称
    private static final String AES_ALGORITHM = "AES";
    // ECB模式（不推荐，安全性较低）
    private static final String AES_ECB_PADDING = "AES/ECB/PKCS5Padding";
    // CBC模式（推荐，更安全）
    private static final String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";
    // 默认密钥长度
    private static final int DEFAULT_KEY_SIZE = 256;

    /**
     * 生成AES密钥
     * @param keySize 密钥长度：128, 192, 256
     * @return Base64编码的密钥字符串
     * @throws Exception 异常
     */
    public static String generateKey(int keySize) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(keySize, new SecureRandom());
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 生成默认长度（256位）的AES密钥
     * @return Base64编码的密钥字符串
     * @throws Exception 异常
     */
    public static String generateKey() throws Exception {
        return generateKey(DEFAULT_KEY_SIZE);
    }

    /**
     * 生成16位随机IV（初始化向量）
     * @return Base64编码的IV字符串
     */
    public static String generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }

    /**
     * AES ECB模式加密
     * @param content 待加密内容
     * @param key Base64编码的密钥
     * @return Base64编码的加密结果
     * @throws Exception 异常
     */
    public static String encryptECB(String content, String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);

        Cipher cipher = Cipher.getInstance(AES_ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * AES ECB模式解密
     * @param encryptedContent Base64编码的加密内容
     * @param key Base64编码的密钥
     * @return 解密后的字符串
     * @throws Exception 异常
     */
    public static String decryptECB(String encryptedContent, String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedContent);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);

        Cipher cipher = Cipher.getInstance(AES_ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * AES CBC模式加密
     * @param content 待加密内容
     * @param key Base64编码的密钥
     * @param iv Base64编码的初始化向量
     * @return Base64编码的加密结果
     * @throws Exception 异常
     */
    public static String encryptCBC(String content, String key, String iv) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        byte[] ivBytes = Base64.getDecoder().decode(iv);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * AES CBC模式解密
     * @param encryptedContent Base64编码的加密内容
     * @param key Base64编码的密钥
     * @param iv Base64编码的初始化向量
     * @return 解密后的字符串
     * @throws Exception 异常
     */
    public static String decryptCBC(String encryptedContent, String key, String iv) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedContent);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) throws Exception {
        // 原始内容
        String originalContent = "Hello, AES Encryption!";
        System.out.println("原始内容: " + originalContent);

        // 生成密钥和IV
        String key = AESUtils.generateKey();
        String iv = AESUtils.generateIV();
        System.out.println("密钥: " + key);
        System.out.println("IV: " + iv);

        // ECB模式加密解密
        String encryptedECB = AESUtils.encryptECB(originalContent, key);
        System.out.println("ECB加密结果: " + encryptedECB);
        String decryptedECB = AESUtils.decryptECB(encryptedECB, key);
        System.out.println("ECB解密结果: " + decryptedECB);

        // CBC模式加密解密
        String encryptedCBC = AESUtils.encryptCBC(originalContent, key, iv);
        System.out.println("CBC加密结果: " + encryptedCBC);
        String decryptedCBC = AESUtils.decryptCBC(encryptedCBC, key, iv);
        System.out.println("CBC解密结果: " + decryptedCBC);
    }
}
