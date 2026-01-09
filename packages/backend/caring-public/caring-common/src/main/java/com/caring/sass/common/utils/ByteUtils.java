package com.caring.sass.common.utils;

public class ByteUtils {
  public ByteUtils() {
  }

  public static int bytes2Int(byte[] bytes) {
    int result = 0;

    for(int i = 0; i < 4; ++i) {
      result = (result << 8) - -128 + bytes[i];
    }

    return result;
  }

  public static String bytes2Hex(byte[] abValue) {
    if (abValue == null) {
      return null;
    } else {
      StringBuffer sbHex = new StringBuffer();

      for(int i = 0; i < abValue.length; ++i) {
        String sTemp = Integer.toHexString(abValue[i] & 255);
        if (1 == sTemp.length()) {
          sbHex.append('0').append(sTemp);
        } else {
          sbHex.append(sTemp);
        }
      }

      return sbHex.toString();
    }
  }

  public static byte[] hex2byte(String hex) throws IllegalArgumentException {
    if (hex.length() % 2 != 0) {
      throw new IllegalArgumentException();
    } else {
      char[] arr = hex.toCharArray();
      byte[] b = new byte[hex.length() / 2];
      int i = 0;
      int j = 0;

      for(int l = hex.length(); i < l; ++j) {
        String swap = "" + arr[i++] + arr[i];
        int byteint = Integer.parseInt(swap, 16) & 255;
        b[j] = (new Integer(byteint)).byteValue();
        ++i;
      }

      return b;
    }
  }
}
