// src/utils/encryption.ts
import CryptoJS from 'crypto-js';
import * as forge from 'node-forge';

// AES加密配置
const AES_CONFIG = {
    KEY_SIZE: 32 as const, // 256位密钥
    IV_SIZE: 16 as const,  // IV固定16字节
    MODE: CryptoJS.mode.CBC,
    PADDING: CryptoJS.pad.Pkcs7
};

// 生成的AES密钥和IV类型
export type AesKeyIv = {
    key: string;        // Base64编码的密钥
    iv: string;         // Base64编码的IV
    keyHex: string;     // 十六进制编码的密钥
    ivHex: string;      // 十六进制编码的IV
};

/**
 * 生成随机的AES密钥和IV
 */
export const generateAesKeyAndIv = (): AesKeyIv => {
    // 生成随机密钥（32字节）
    const key = CryptoJS.lib.WordArray.random(AES_CONFIG.KEY_SIZE);
    // 生成随机IV（16字节）
    const iv = CryptoJS.lib.WordArray.random(AES_CONFIG.IV_SIZE);

    return {
        key: CryptoJS.enc.Base64.stringify(key),
        iv: CryptoJS.enc.Base64.stringify(iv),
        keyHex: key.toString(CryptoJS.enc.Hex),
        ivHex: iv.toString(CryptoJS.enc.Hex)
    };
};

/**
 * AES加密
 * @param plainText 明文
 * @param keyBase64 Base64编码的AES密钥
 * @param ivBase64 Base64编码的IV
 */
export const aesEncrypt = (
    plainText: string,
    keyBase64: string,
    ivBase64: string
): string => {
    try {
        const key = CryptoJS.enc.Base64.parse(keyBase64);
        const iv = CryptoJS.enc.Base64.parse(ivBase64);

        const ciphertext = CryptoJS.AES.encrypt(plainText, key, {
            iv: iv,
            mode: AES_CONFIG.MODE,
            padding: AES_CONFIG.PADDING
        });

        return ciphertext.toString();
    } catch (error) {
        console.error('AES加密失败:', error);
        throw new Error('AES加密失败');
    }
};

/**
 * AES解密
 * @param cipherText 密文
 * @param keyBase64 Base64编码的AES密钥
 * @param ivBase64 Base64编码的IV
 */
export const aesDecrypt = (
    cipherText: string,
    keyBase64: string,
    ivBase64: string
): string => {
    try {
        const key = CryptoJS.enc.Base64.parse(keyBase64);
        const iv = CryptoJS.enc.Base64.parse(ivBase64);

        const bytes = CryptoJS.AES.decrypt(cipherText, key, {
            iv: iv,
            mode: AES_CONFIG.MODE,
            padding: AES_CONFIG.PADDING
        });

        return bytes.toString(CryptoJS.enc.Utf8);
    } catch (error) {
        console.error('AES解密失败:', error);
        throw new Error('AES解密失败');
    }
};
const formatPublicKey = (publicKey: string): string => {
    if (publicKey.startsWith('-----BEGIN PUBLIC KEY-----')) {
        return publicKey;
    }

    // 处理换行符
    let formattedKey = publicKey.replace(/(.{64})/g, '$1\n');
    if (formattedKey.endsWith('\n')) {
        formattedKey = formattedKey.slice(0, -1);
    }

    return `-----BEGIN PUBLIC KEY-----\n${formattedKey}\n-----END PUBLIC KEY-----`;
}
/**
 * RSA加密（使用公钥加密AES密钥）
 * @param plainText 明文
 * @param publicKey RSA公钥
 */
export const rsaEncrypt = (plainText: string, publicKey: string): string => {
    try {
        const publicKeyPem = formatPublicKey(publicKey);
// 解析公钥
        const publicKey1 = forge.pki.publicKeyFromPem(publicKeyPem);

        // 创建 SHA-256 哈希
        const md = forge.md.sha256.create();

        // 使用 OAEP 填充加密
        const encrypted = publicKey1.encrypt(plainText, 'RSA-OAEP', {
            md: md,
            mgf1: {
                md: forge.md.sha256.create()
            }
        });

        // 转换为 Base64
        return forge.util.encode64(encrypted);
    } catch (error) {
        console.error('RSA加密失败:', error);
        throw new Error('RSA加密失败');
    }
};
