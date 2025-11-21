package top.xinsin.util;

import org.springframework.stereotype.Component;

@Component
public class AESComponent {

    public String encrypt(String content, String aesKey, String aesIv) throws Exception {
        return AESUtils.encryptCBC(content, aesKey, aesIv);
    }

    public String decrypt(String encryptedContent, String aesKey, String aesIv) throws Exception {
        return AESUtils.decryptCBC(encryptedContent, aesKey, aesIv);
    }
}
