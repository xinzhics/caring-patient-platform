package com.caring.sass.common.utils.key;

import java.util.Properties;

public final class Key {
  public Key() {
  }

  public static final String key() {
    Properties props = new Properties();
    props.setProperty("separator", "");
    HexUUIDGenerator gen = new HexUUIDGenerator();
    gen.configure(props);
    return (String)gen.generate();
  }

  public static final String generate6Number() {
    String s;
    for(s = ""; s.length() < 6; s = s + (int)(Math.random() * 10.0D)) {
    }

    return s;
  }

  public static String generate8bitCode() {
    String chars = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678";
    int MaxPos = chars.length();
    String code = "";

    for(int i = 0; i < 8; ++i) {
      code = code + chars.charAt((int)Math.floor(Math.random() * (double)MaxPos));
    }

    return code;
  }

  public static String key(int i) {
    Properties props = new Properties();
    props.setProperty("separator", "");
    HexUUIDGenerator gen = new HexUUIDGenerator();
    gen.configure(props);
    return (String)gen.generate(20);
  }
}
