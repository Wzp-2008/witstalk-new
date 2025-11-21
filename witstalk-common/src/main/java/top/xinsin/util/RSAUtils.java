package top.xinsin.util;

import jakarta.xml.bind.DatatypeConverter;
import top.xinsin.entity.KeyPairVO;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

/**
 * RSA非对称加密工具类
 * 支持密钥对生成、加密、解密、签名、验签
 */
public class RSAUtils {

    // 算法名称
    private static final String RSA_ALGORITHM = "RSA";
    // 签名算法（RSA+SHA256）
    private static final String SIGN_ALGORITHM = "SHA256withRSA";
    // 默认密钥长度（2048位，推荐生产环境使用4096位）
    private static final int DEFAULT_KEY_SIZE = 2048;
    // 加密填充方式（适配大部分场景，避免长度限制问题）
    private static final String RSA_PADDING = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * 生成RSA密钥对（公钥+私钥）
     * @param keySize 密钥长度：1024（不推荐）、2048、4096
     * @return 密钥对（公钥、私钥均为Base64编码字符串）
     * @throws Exception 异常
     */
    public static KeyPairVO generateKeyPair(int keySize) throws Exception {
        // 创建密钥对生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(keySize, new SecureRandom());

        // 生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 转换为Base64编码字符串（便于存储和传输）
        String publicKeyStr = DatatypeConverter.printBase64Binary(publicKey.getEncoded());
        String privateKeyStr = DatatypeConverter.printBase64Binary(privateKey.getEncoded());

        return new KeyPairVO(publicKeyStr, privateKeyStr);
    }

    /**
     * 生成默认长度（2048位）密钥对
     * @return 密钥对VO
     * @throws Exception 异常
     */
    public static KeyPairVO generateKeyPair() throws Exception {
        return generateKeyPair(DEFAULT_KEY_SIZE);
    }

    /**
     * 公钥加密（非对称加密：公钥加密，私钥解密）
     * @param content 待加密内容（明文）
     * @param publicKeyStr Base64编码的公钥
     * @return Base64编码的加密结果（密文）
     * @throws Exception 异常
     */
    public static String encryptByPublicKey(String content, String publicKeyStr) throws Exception {
        // 1. 解码Base64公钥字符串
        byte[] publicKeyBytes = DatatypeConverter.parseBase64Binary(publicKeyStr);

        // 2. 构建公钥对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // 3. 初始化加密器
        Cipher cipher = Cipher.getInstance(RSA_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // 4. 加密并返回Base64编码结果
        byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printBase64Binary(encryptedBytes);
    }

    /**
     * 私钥解密
     * @param encryptedContent Base64编码的密文
     * @param privateKeyStr Base64编码的私钥
     * @return 解密后的明文
     * @throws Exception 异常
     */
    public static String decryptByPrivateKey(String encryptedContent, String privateKeyStr) throws Exception {
        // 1. 解码Base64私钥字符串
        byte[] privateKeyBytes = DatatypeConverter.parseBase64Binary(privateKeyStr);

        // 2. 构建私钥对象
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // 3. 初始化解密器
        Cipher cipher = Cipher.getInstance(RSA_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // 4. 解密并返回明文
        byte[] decryptedBytes = cipher.doFinal(DatatypeConverter.parseBase64Binary(encryptedContent));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 私钥签名（防止数据篡改，验证数据来源）
     * @param content 待签名内容
     * @param privateKeyStr Base64编码的私钥
     * @return Base64编码的签名结果
     * @throws Exception 异常
     */
    public static String sign(String content, String privateKeyStr) throws Exception {
        // 1. 构建私钥对象
        byte[] privateKeyBytes = DatatypeConverter.parseBase64Binary(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // 2. 初始化签名器
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));

        // 3. 生成签名并返回Base64编码
        byte[] signBytes = signature.sign();
        return DatatypeConverter.printBase64Binary(signBytes);
    }

    /**
     * 公钥验签（验证数据是否被篡改、是否来自合法来源）
     * @param content 原始内容
     * @param signStr Base64编码的签名
     * @param publicKeyStr Base64编码的公钥
     * @return true-验签通过，false-验签失败
     * @throws Exception 异常
     */
    public static boolean verifySign(String content, String signStr, String publicKeyStr) throws Exception {
        // 1. 构建公钥对象
        byte[] publicKeyBytes = DatatypeConverter.parseBase64Binary(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // 2. 初始化验签器
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));

        // 3. 验签并返回结果
        byte[] signBytes = DatatypeConverter.parseBase64Binary(signStr);
        return signature.verify(signBytes);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) throws Exception {
        // 1. 生成密钥对
        KeyPairVO keyPair = RSAUtils.generateKeyPair();
        System.out.println("公钥：" + keyPair.getPublicKey());
        System.out.println("私钥：" + keyPair.getPrivateKey());

        // 2. 原始数据
        String originalContent = "这是需要加密的敏感数据：123456";
        System.out.println("原始内容：" + originalContent);

        // 3. 公钥加密
        String encrypted = RSAUtils.encryptByPublicKey(originalContent, keyPair.getPublicKey());
        System.out.println("加密后：" + encrypted);

        // 4. 私钥解密
        String decrypted = RSAUtils.decryptByPrivateKey(encrypted, keyPair.getPrivateKey());
        System.out.println("解密后：" + decrypted);

        // 5. 私钥签名
        String sign = RSAUtils.sign(originalContent, keyPair.getPrivateKey());
        System.out.println("签名结果：" + sign);

        // 6. 公钥验签
        boolean verifyResult = RSAUtils.verifySign(originalContent, sign, keyPair.getPublicKey());
        System.out.println("验签结果：" + verifyResult);
    }
}
