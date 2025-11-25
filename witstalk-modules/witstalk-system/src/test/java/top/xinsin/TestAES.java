package top.xinsin;

import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import top.xinsin.util.AESUtils;
import top.xinsin.util.RSAUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class TestAES {
    @Test
    @SneakyThrows
    public void testRSA(){
        String privateRSAKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCI4muYlJ/MP+I6dpdBpgQo7WvXIS1nCy8I+KPP4wYq32nED7hULMyjAJu9y1p+TJov0wO6Q1FgZRMN0qojUNQoFjapS/35VkX1HQyVhbmhD4WamohijLZIxGKjBQFU3lHV2+yU3j0VjC3DzY9gUQoqyAAJPvkQN1crTIjaJYQQDkBwzzrUcX/U1MbH1yzhfUUx5eXgrcE2eLhFMR4J9UdJoJSQ3hIYB0aCIemPzbXyMDb/J/M5YSz4DsCEHvmRFWg3pU0V7YKakvj3pWWL4RGpHRguuEp8FecO30WEa6TP+6jgx+Je6BLFiXySUYttiPsMfpIJCTc41E3k1H7j0KZ/AgMBAAECggEADYCMDXZnls2cYpjtDZgzI9hbgVJv+JCVWKSx/+EMbDEybRkB3Se4HcIDGsMSz499zAbcAMUAI2qE6SGlD6u66aNbS/LHgA8/AlEVuzAAIf92dcwVdOcdWespMZxeR8FQQDCb0wli7BuyLfS6Vk1YG+Uzf7X6+lsDGa3IeAAoWS7xw+/j5GsawCay5y9vwr/4dZbSmJaguxeXq7Q+RwUEwrRaX4JasaGLhb7zYxs35w1o5o3qINWP06dvU122UTlofLer03m5nWPbq7t6KLq+R3k/5Pp8la/aaHpXtoNnwqQqEXT1v27tEwztgcYbcf6SJZk8qQ13rOwBg3eGwe+/DQKBgQC4Mv14cbItyuY584LmP+0x/rx3Nel4egr7MLkacpcYQv3VTRPi0rQSdunuWkkLbfbYTDDo6VaBcRHFEjGmkuOdR7QDLExMFuByUydpmv6wayFJu2xrXF40IcLLTGkcTAc8cq/dG366f9/oQ6ov3ut5G2Y5V8TSiqxanXFoi8mBSwKBgQC+PfOqGSEgw+E9+AQWErGCedurKTQauxvpqKilCKJxzdLhAp4UvSDJB9qJpraVKTBm9RDzq7/04SEZWgGxDC8652HpdXVfBYes5LgLGZP9UgpTTWcshFiN061y4OctkfTpB4vTRXvRfcvuStPmZNDdRkf+8gJy4bB/K4vJwddjHQKBgFp5+VTzC+FAW4u1l+N/HQREqtAndBGQaNpvWUyrYpoxNbtTC+SLOJYlQMyqHlz125zs980/6BTWtDLHuAmo9xKnSL2SQbvSNNcTDzXpQxbm2Ef+PsgRa9hRiPwRAUQ7+AXyE34Y5ku3SgUtT2gKKEGperHJ8cy0/unXkgAXQZg3AoGBAL0fG1+K8cr/z6hqKa3D0aGzALTMFldIkTwdBeJijhLymWRE1OArEC+pVhCnojTwqISBteN3ZzHKqLvqJWpbgYxeP5D1ysO23KT9DxVBzUP0zfz4Isw3mU0NFQ3yFP/RbJZIgTF7jta45wWyb6tHp/eBicshzVtlj68snB/rSvLlAoGBALXGNtIqYwh5pdHOe7zwFjdIGCyLI2/7oOMl1WCCRSPuBjsavdI3yiMngpqYp0E44Nn8wBQsT2HQavavb3iNQLZGdXkhW65yhOL5ElTKjFqA5ZgzZJRzDKuXKGtKqzeFWxk86laXkCzJ0DI2BYq0586yYpZXrXTI639Zzjn0zcYX";
        String publicRSAKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiOJrmJSfzD/iOnaXQaYEKO1r1yEtZwsvCPijz+MGKt9pxA+4VCzMowCbvctafkyaL9MDukNRYGUTDdKqI1DUKBY2qUv9+VZF9R0MlYW5oQ+FmpqIYoy2SMRiowUBVN5R1dvslN49FYwtw82PYFEKKsgACT75EDdXK0yI2iWEEA5AcM861HF/1NTGx9cs4X1FMeXl4K3BNni4RTEeCfVHSaCUkN4SGAdGgiHpj8218jA2/yfzOWEs+A7AhB75kRVoN6VNFe2CmpL496Vli+ERqR0YLrhKfBXnDt9FhGukz/uo4MfiXugSxYl8klGLbYj7DH6SCQk3ONRN5NR+49CmfwIDAQAB";
//        String s = "Grh4sSZV68fU3bP546Nq0YVJgDaox4o+NQGjeEmjr1FLEkyrThRGyZZ4eNhClyWxMdHI28/kkRlXGzAnp/yeysUKUlWPa8nfRbsiBxt1LKnweEHam7Uf8/jPvwAUNaJhPJOPUCXKavbJc4frDyMRkLAJWWdBJUimlmVOJrJraqN08q/IPC+GCaSbfBq3OBPt5uE0E3Yxni9Wl7PtypdRpPhPhRL2Fe2LnCfX91RlIKfFcVZl30f6YHHW+S8qvkee7Nh8y1GAec14Qfpmh3r9ypMnBXihTVjjpeNFvFFZV1y4p8Y+wt+adsXy61RjVkstjvYXEAp7LdVhAJknri9/fg==";
        String s = "HMQdYgqljZk72XguxGerdRbJSVBqE4oT9ARnVPrWYXy25UdGodfcneGJQb+QMgy2mD8ItBH6WWbXwuj/c5ivBNMPR/FlXhfBitX3zvmnWN19UzsQ0GH8P8+sdhpnd3YAxg0DWVPCHjUOwZXBKSu0YmX3ZcFk77giVJWw+HmN7jTHgM9FE1LY5eR3IYfPdGlJfO2faryjaR4qp6+RByLy12eK2x8gA262xkA75li/6nhNJSsYwxOiMBmYwMFyT0opkUUY3xSKlwshDyHa2em8Xye8Yrko2rHx7doSHg7Z/ATlprjOjEI4t3x4xxd67MIUn1m72GnuimXo/+1hnf4afQ==";
        String sourceText = "eM6oj6fYyhoHtR7Bdmde2b6kkv1PPpO5CN4YLwY25zA=/y0mxpAmhEbxj+QB/hqeHg==";
        System.out.println(RSAUtils.encryptByPublicKey(sourceText, publicRSAKey));
        System.out.println(RSAUtils.decryptByPrivateKey(s, privateRSAKey));
    }
    @Test
    @SneakyThrows
    public void test() {
        long oldTime = System.currentTimeMillis();
        String publicRSAKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtTMK9q+gM5mdpgfm5irPHg8tiE4UKS7VOP1MWM3xnEi88+eqXzNXgHytJpgVztUWZIqo6Gd4Me6RZPwu8kYyQK26IX/GXz9wFxRdIqceGUMoXBlfeedPrCLZFqlpAVRvqNBtC0jemK0uVJtSbcfGOnzvJKH051gT5TS5lGOMzHySOg9A+4e9xqiF3lxhkKfezgJCVEJXYH1gv5RR2fUdPfrxdPfI94caA9omdjR+HfiP+JvI+3wZWZmvF3uaLrJYmjqCrYq966wwjX3r4kwEudrXrexl/uutD9omINxXMA93xHLVS22X4KvZ5ZRoO5lRUbjZOHSsEFCL/Mj5SNGwcwIDAQAB";
//        生成的AES密钥
        String AESKey = AESUtils.generateKey();
//        生成的IV密钥
        String AESIV = AESUtils.generateIV();
        System.out.println("前端请求原数据: " + getText());
        JSONObject jsonObject = new JSONObject().fluentPut("data", AESUtils.encryptCBC(getText(), AESKey, AESIV));
        System.out.println("前端AES加密后的数据" + jsonObject.toJSONString());
//        这里进行 key和iv的混合在进行rsa加密
        String s = AESKey + "|" + AESIV;
        System.out.println("前端AES的key和iv混合数据: " + s);
        String requestJSON = RSAUtils.encryptByPublicKey(s, publicRSAKey);
        System.out.println("前端RSA加密后key和iv的数据: " + requestJSON);
        jsonObject.fluentPut("key", requestJSON);
        System.out.println("前端AES加密业务数据并使用RSA加密key和iv后的数据: " + jsonObject.toJSONString());
        String string = Base64.getEncoder().encodeToString(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
        System.out.println("前端base64编码后的数据: " + string);
        System.out.println("此时将数据发送至后端服务器...");
        String backData = returnText(string);
        System.out.println("后端返回的数据: " + backData);
        System.out.println("前端使用aes解密后数据: " + AESUtils.decryptCBC(backData, AESKey, AESIV));
        long newTime = System.currentTimeMillis();
        System.out.println("耗时: " + (newTime - oldTime) + "ms");
    }
    @SneakyThrows
    private String returnText(String content) {
        String privateRSAKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC1Mwr2r6AzmZ2mB+bmKs8eDy2IThQpLtU4/UxYzfGcSLzz56pfM1eAfK0mmBXO1RZkiqjoZ3gx7pFk/C7yRjJArbohf8ZfP3AXFF0ipx4ZQyhcGV9550+sItkWqWkBVG+o0G0LSN6YrS5Um1Jtx8Y6fO8kofTnWBPlNLmUY4zMfJI6D0D7h73GqIXeXGGQp97OAkJUQldgfWC/lFHZ9R09+vF098j3hxoD2iZ2NH4d+I/4m8j7fBlZma8Xe5ousliaOoKtir3rrDCNfeviTAS52tet7GX+660P2iYg3FcwD3fEctVLbZfgq9nllGg7mVFRuNk4dKwQUIv8yPlI0bBzAgMBAAECggEAAsjfMm2o3BzQapSfgfUGzChL46wzgh3ZTVq92XWHcNvrT3eay4Np+lcFgDIrz6BrC4OCrK5Tugy3s6IZL0RQkbmPF+Rd+OkW1hFMNx6eB0xcFUJB5d6Iqmq6BFpOe09ADLZF9BDxdCOL3TqqgyEOqlJw3b1sGpa6GOEnuEs+sYZynJLIBQHup4NJzrGh8yHFRgW2xUgppTEeNqeZNh6uu3yE6Siqr1paVHPXN28ayeXOHXCJyPxEOeqjja7WmmQtXp1nluPUeKdLLYUacYRN5BzOUXIKQgI8xgLUSwEoH0KXiyVpx2rGie8Zx1rvvoXibHt5YYhzH8mZp6NVXEq/eQKBgQDek6/ogI40bKwXVbu82WyHQeOLJfz/Yxk+l6s+h8yMLwyf8wB6QWubzjwaa06sRkoHNQAKBRWDYX/Gyh1ZcblKdJfIt0anLw6V9xuHp9HOjYuZl12WquoANyxfiNAUGsvwXAA61Oktwg6XJCLJ1ammm/ec5rghyzKauUHPtJfk2QKBgQDQaLgSl7WLhqa8EAj9UHzb/AvfztgF7Vp00letVwPKatZTHVWFL2zz/GJFq9aOBMZL9AhkeA+aXuiFDGkm9yadLNo2ZGNYfQvKa5xlV3m6BzEY/7fEATr2iY+UDf/cHp0tPcG22NP7y5bv3MFypb6fury8zfzvNAeskzdEG9hAKwKBgCVKURp/D1QxMNPoBUAqnUg7/cd+YdjX3Y7jEkmzrs19xHu31sik1UaRUviKKfArBpQWiR8kouoUF7hSfIBT0VtsFVIHgsrX03XEpNQhhcE5bb4Z4hAwKmxG0iRfY506sBhhEZjJuxdzf8ZKw5lVNss3LlEXRppC1QOfLiuCckBhAoGAKAvHOwNdxha0mTR3lb1OkLLI0VOcnN7z37i/UjaDKvUGg+gCE9p+FA/uDjcrvhRayiN1LWEIloAgK9irp+obyF3i/Eqj8E/u4RF2dt6tLDrPi18vv4EBBHiAV1GtVW2ohBX20Byv9xUyxppjrZT9oRGvzJPEQ256/wU2vGahUgsCgYEAu6DSVvgXVYDyAlzhmQ/N1xOOhqQC4ihPmhHMP7cWbmDAnz28tCW0YW1jpl5gpHG1RBmpbU/QJI3IgOXfITHgpVPT2u/2lUY3G2VjGVGs8JK+JGJWY4CVoT+PO3ly/SHjZbwDCuO+2H1gGhs9ozFOR7THOe4TMwzyt6lKKZq/z7E=";
        String base64Content = new String(Base64.getDecoder().decode(content), StandardCharsets.UTF_8);
        System.out.println("后端base64解密后的数据: " + base64Content);
        JSONObject jsonObject1 = JSONObject.parseObject(base64Content);
        String s = RSAUtils.decryptByPrivateKey(jsonObject1.getString("key"), privateRSAKey);
        System.out.println("后端RSA解密后的AES的key和iv的混合的数据: " + s);
        String[] split = s.split("\\|");
        String AESKey = split[0];
        String AESIv = split[1];
        System.out.println("后端取出的AESKEY: " + AESKey);
        System.out.println("后端取出的AESIv: " + AESIv);
        String decryptedText = AESUtils.decryptCBC(jsonObject1.getString("data"), AESKey, AESIv);
        System.out.println("后端AES解密后的原数据: " + decryptedText);
        System.out.println("---------------------------此时请求流程完成,以下为返回流程---------------------------------------------");
        System.out.println("后端返回的原数据: " + getBackText());
        String s1 = AESUtils.encryptCBC(getBackText(), AESKey, AESIv);
        System.out.println("后端使用前端的aes加密后数据: " + s1);
        return s1;
    }

    private String getBackText() {
        return "{\"code\":200,\"msg\":\"success\",\"error\":null,\"data\":[],\"timestamp\":1763628798628}";
    }
    private String getText() {
        return "{\"code\":200,\"msg\":\"success\",\"error\":null,\"data\":[{\"id\":1,\"remark\":null,\"createBy\":null,\"createTime\":\"2025-11-20 14:47:11\",\"updateBy\":null,\"updateTime\":null,\"delFlag\":\"0\",\"username\":\"admin\",\"password\":\"$2a$10$QhGqe.lTRYQKYDvqE9kX8.hCSuyApDiDSgc0EBFZlj27CU2w5mU16\",\"nickName\":\"管理员\"},{\"id\":2,\"remark\":null,\"createBy\":null,\"createTime\":\"2025-11-20 14:48:15\",\"updateBy\":null,\"updateTime\":null,\"delFlag\":\"0\",\"username\":\"admin1\",\"password\":\"$2a$10$DF8qsisp.xfbCaP6VSX4M.YyDCFcF6KfENJa.4mB09nhCt/m0/iG2\",\"nickName\":\"管理员\"}],\"timestamp\":1763628798628}";
    }
}
