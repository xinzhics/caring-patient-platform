package com.caring.sass.ai.signer;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class Utils {


    public static String hashSHA256(byte[] content) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Hex.encodeHexString(md.digest(content));
        } catch (Exception e) {
            throw new Exception(
                    "Unable to compute hash while signing request: "
                            + e.getMessage(), e);
        }
    }

    public static byte[] hmacSHA256(byte[] key, String content) throws Exception {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(content.getBytes());
        } catch (Exception e) {
            throw new Exception(
                    "Unable to calculate a request signature: "
                            + e.getMessage(), e);
        }
    }




}
