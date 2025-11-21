package top.xinsin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RSAComponent {

    // 从配置文件读取公钥、私钥（避免硬编码，推荐用配置中心存储）
    @Value("${app.rsa.public-key}")
    private String publicKey;

    @Value("${app.rsa.private-key}")
    private String privateKey;

    /**
     * 加密敏感数据
     */
    public String encrypt(String content) throws Exception {
        return RSAUtils.encryptByPublicKey(content, publicKey);
    }

    /**
     * 解密数据
     */
    public String decrypt(String encryptedContent) throws Exception {
        return RSAUtils.decryptByPrivateKey(encryptedContent, privateKey);
    }

    /**
     * 对响应数据签名
     */
    public String signData(String content) throws Exception {
        return RSAUtils.sign(content, privateKey);
    }

    /**
     * 验证请求数据签名
     */
    public boolean verifyDataSign(String content, String sign) throws Exception {
        return RSAUtils.verifySign(content, sign, publicKey);
    }
}
