package com.caring.sass.common.mybaits;

import cn.hutool.core.util.StrUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;


public class PhoneEncryptionUtil {
    public static final String ALGORITHM  = "AES";

    public static String encrypt(String value) throws Exception {
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encValue);
    }

    public static String decrypt(String encryptedValue) throws Exception {
        if (StrUtil.isEmpty(encryptedValue)) {
            return null;
        }
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedValue);
        byte[] decValue = c.doFinal(decordedValue);
        return new String(decValue);
    }

    private static Key generateKey() throws Exception {
        // 测试 "sva4yAdvVa6I6Eyo"
        return new SecretKeySpec("sva6yAavVa6I6Eyi".getBytes(), ALGORITHM);
    }

    public static void main(String[] args) {
        String str  = "15726673771";
        String decrypt = null;
        try {
            decrypt = encrypt(str);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(decrypt);
    }
}