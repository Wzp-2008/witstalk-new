package top.xinsin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyPairVO {
    private String publicKey;  // Base64编码公钥
    private String privateKey; // Base64编码私钥
}
