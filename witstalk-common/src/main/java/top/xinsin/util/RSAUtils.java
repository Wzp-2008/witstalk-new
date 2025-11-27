package top.xinsin.util;

import jakarta.xml.bind.DatatypeConverter;
import top.xinsin.entity.KeyPairVO;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.spec.PSource;

/**
 * RSA非对称加密工具类（Oracle JDK 17 专用）
 * 支持密钥对生成、加密、解密、签名、验签
 */
public class RSAUtils {

    // 算法名称
    private static final String RSA_ALGORITHM = "RSA";
    // 签名算法（RSA+SHA256）
    private static final String SIGN_ALGORITHM = "SHA256withRSA";
    // 默认密钥长度（2048位）
    private static final int DEFAULT_KEY_SIZE = 2048;

    // Oracle JDK 17 支持的填充方式
    private static final String RSA_PADDING_PKCS1 = "RSA/ECB/PKCS1Padding";
    private static final String RSA_PADDING_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    // 静态初始化：确保使用最新的安全提供程序
    static {
        Security.setProperty("crypto.policy", "unlimited");
    }

    /**
     * 生成RSA密钥对
     */
    public static KeyPairVO generateKeyPair(int keySize) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(keySize, new SecureRandom());

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String publicKeyStr = DatatypeConverter.printBase64Binary(publicKey.getEncoded());
        String privateKeyStr = DatatypeConverter.printBase64Binary(privateKey.getEncoded());

        return new KeyPairVO(publicKeyStr, privateKeyStr);
    }

    public static KeyPairVO generateKeyPair() throws Exception {
        return generateKeyPair(DEFAULT_KEY_SIZE);
    }

    /**
     * 公钥加密（支持PKCS1和OAEP两种模式）
     */
    public static String encryptByPublicKey(String content, String publicKeyStr) throws Exception {
        return encryptByPublicKey(content, publicKeyStr, true);
    }

    public static String encryptByPublicKey(String content, String publicKeyStr, boolean usePkcs1) throws Exception {
        // 解码公钥
        byte[] publicKeyBytes = DatatypeConverter.parseBase64Binary(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // 选择填充方式
        String padding = usePkcs1 ? RSA_PADDING_PKCS1 : RSA_PADDING_OAEP;
        Cipher cipher = Cipher.getInstance(padding);

        if (usePkcs1) {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        } else {
            // Oracle JDK 17 需要明确的 OAEP 参数配置
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                    "SHA-256", "MGF1",

                    new MGF1ParameterSpec("SHA-256"), // 现在可以正确解析了
                    PSource.PSpecified.DEFAULT
            );
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
        }

        // 检查数据长度限制
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        int maxLength = usePkcs1 ? (DEFAULT_KEY_SIZE / 8 - 11) : (DEFAULT_KEY_SIZE / 8 - 66);

        if (contentBytes.length > maxLength) {
            throw new IllegalArgumentException(
                    String.format("明文过长：%d字节，最大支持：%d字节",
                            contentBytes.length, maxLength)
            );
        }

        // 加密并返回Base64结果
        byte[] encryptedBytes = cipher.doFinal(contentBytes);
        return DatatypeConverter.printBase64Binary(encryptedBytes);
    }

    /**
     * 私钥解密
     */
    public static String decryptByPrivateKey(String encryptedContent, String privateKeyStr) throws Exception {
        return decryptByPrivateKey(encryptedContent, privateKeyStr, true);
    }

    public static String decryptByPrivateKey(String encryptedContent, String privateKeyStr, boolean usePkcs1) throws Exception {
        // 解码私钥
        byte[] privateKeyBytes = DatatypeConverter.parseBase64Binary(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // 选择填充方式
        String padding = usePkcs1 ? RSA_PADDING_PKCS1 : RSA_PADDING_OAEP;
        Cipher cipher = Cipher.getInstance(padding);

        if (usePkcs1) {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
        } else {
            // 解密时使用相同的OAEP参数
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                    "SHA-256", "MGF1",
                    new MGF1ParameterSpec("SHA-256"), // 现在可以正确解析了
                    PSource.PSpecified.DEFAULT
            );
            cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
        }

        // 解密
        byte[] encryptedBytes = DatatypeConverter.parseBase64Binary(encryptedContent);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 签名和验签方法
     */
    public static String sign(String content, String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = DatatypeConverter.parseBase64Binary(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));

        byte[] signBytes = signature.sign();
        return DatatypeConverter.printBase64Binary(signBytes);
    }

    public static boolean verifySign(String content, String signStr, String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = DatatypeConverter.parseBase64Binary(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));

        byte[] signBytes = DatatypeConverter.parseBase64Binary(signStr);
        return signature.verify(signBytes);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) throws Exception {
        // 1. 生成密钥对
        KeyPairVO keyPair = RSAUtils.generateKeyPair();
        System.out.println("=== Oracle JDK 17 RSA测试 ===");
        System.out.println("公钥：" + keyPair.getPublicKey());
        System.out.println("私钥：" + keyPair.getPrivateKey());

        // 2. 测试PKCS1模式（兼容性最好）
        String originalContent = "Oracle JDK 17 RSA测试数据";
        System.out.println("\n原始内容：" + originalContent);

        String encryptedPkcs1 = RSAUtils.encryptByPublicKey(originalContent, keyPair.getPublicKey(), true);
        System.out.println("PKCS1加密后：" + encryptedPkcs1);

        String decryptedPkcs1 = RSAUtils.decryptByPrivateKey(encryptedPkcs1, keyPair.getPrivateKey(), true);
        System.out.println("PKCS1解密后：" + decryptedPkcs1);

        // 3. 测试OAEP模式（更安全）
        String encryptedOaep = RSAUtils.encryptByPublicKey(originalContent, keyPair.getPublicKey(), false);
        System.out.println("\nOAEP加密后：" + encryptedOaep);

        String decryptedOaep = RSAUtils.decryptByPrivateKey(encryptedOaep, keyPair.getPrivateKey(), false);
        System.out.println("OAEP解密后：" + decryptedOaep);

        // 4. 签名验签
        String sign = RSAUtils.sign(originalContent, keyPair.getPrivateKey());
        System.out.println("\n签名：" + sign);
        System.out.println("验签结果：" + RSAUtils.verifySign(originalContent, sign, keyPair.getPublicKey()));
    }
}
