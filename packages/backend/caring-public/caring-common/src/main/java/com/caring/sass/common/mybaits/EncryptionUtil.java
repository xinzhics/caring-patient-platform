package com.caring.sass.common.mybaits;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.common.constant.ApplicationProperties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;


public class EncryptionUtil {
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
        // 生产 "sva6yAavVa6I6Eyi"
        return new SecretKeySpec(ApplicationProperties.getEncryptionKey().getBytes(), ALGORITHM);
    }

    public static void main(String[] args) {
//        String mobile  = "qqPGwXbTgCsLSPKEhBMNNg==\n" +
//                "                LS9opNqaVwnfK6/Yemm6LQ==\n" +
//                "                eloi4KY5qPySQsVIeciSsg==\n" +
//                "                7TYfT1lMJckSEpI2Z1d2hA==\n" +
//                "                6sg2734lUui69Ns/v4rbjQ==\n" +
//                "                7014rPdS005g4tAJlv12dg==\n" +
//                "                O/7utKfpa7bZuFQy6eXmwQ==\n" +
//                "                VpoNz0uR8hUWwP/m3oSeEA==\n" +
//                "                ddv3SL7XP8/xyNestdkqbQ==\n" +
//                "                tDHKhj5jFkUGrUnN1kjsCA==\n" +
//                "                9+5XbYhSFRPf8UL+8PfkuA==\n" +
//                "                25ynu9JfxtFtbmH987zU5g==\n" +
//                "                9XtX4BbP76I+PKkhrOc8pA==\n" +
//                "                EkvkW/Zqi2MCuKCl2KzadA==\n" +
//                "                ses9e52OMfIaMb5UDhzIpw==\n" +
//                "                MrMCiJSorE7y3DnTPcb25Q==\n" +
//                "                PX+btbuYR1hFi7YUoW9XVw==\n" +
//                "                QcGgFaQQ+WHdd7NKrIxVjg==\n" +
//                "                hx/Z81pL8yKQUa55aSDUdQ==\n" +
//                "                p7o+mzbOWK19lD3DHkwfoA==\n" +
//                "                RtUqqDTAz2qBX1wwzy7Lpg==\n" +
//                "                rXqqteh9BqWZn1z6JGM9nw==\n" +
//                "                LFHWF3oCpaVmqnNa4G8MJw==\n" +
//                "                VpC3RGOAreqjQuoMegJx2g==\n" +
//                "                ze+cICEx1Mw9IKz89JQWeA==\n" +
//                "                izqAZn61UCy33JtDQEzjjQ==\n" +
//                "                xvpbTQR8+IJu6ZtagJQCcw==\n" +
//                "                pt6FunBGrpsx/+3dgXNx1A==\n" +
//                "                z54YoaxjIDgPBFwWgU4ojg==\n" +
//                "                ZDVrIYDKo1prohZhgrmx6A==\n" +
//                "                su+c7mvIbYofUaoKlDeHRw==\n" +
//                "                niZn0s/ZAZ//xsBrRoCmvg==\n" +
//                "        DZsz5DKy+TqchPROglPrqA==\n" +
//                "                3+Ca/1kV5LgP5mHUJ4PW4g==\n" +
//                "                3EsSC6+0VvWuflDEXJHnIA==\n" +
//                "                zQUmFr9Zu3NTb26JuOX54g==\n" +
//                "                2atgseUzsZ354dK8guzeFw==\n" +
//                "                NARq0wF7TyJpmRJBgQDwig==\n" +
//                "                h86/cehG5fGR/oPbdYlEEA==\n" +
//                "                RfO2/IYqEFl3QqOiNlQG3w==\n" +
//                "                hv8PvC4VIVYDhOCd4qqJNQ==\n" +
//                "                THRsTPXP5cdCkl+lmdFSfA==\n" +
//                "                3gGjHkEYWIJW1CFuC5SMHQ==\n" +
//                "                UeBGwKJE6x15oNmUbhFlFQ==\n" +
//                "                FIS/t7CySKrK3cX+qzEZpg==\n" +
//                "                2mGFGGQvleDSwEeZdID0MA==\n" +
//                "                MBc+sn/IoLUY04t1grHtjA==\n" +
//                "                lS1ZI8o7wFZ+WrEwNiHh6g==\n" +
//                "                HDaB8UDgryFXKJPQXtdEow==";
//        String[] mobiles = mobile.split("\n");
//
//        for (String s : mobiles) {
//            String decrypt = null;
//            try {
//            decrypt = decrypt(s.trim());
////                decrypt = encrypt(s);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            System.out.println(decrypt);
//        }
//        String str  = "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";
        String str  = "15173786554";
        String decrypt = null;
        try {
//            decrypt = decrypt("6sg2734lUui69Ns/v4rbjQ==");
            decrypt = encrypt(str);
            String result = decrypt(decrypt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(decrypt);
    }
}