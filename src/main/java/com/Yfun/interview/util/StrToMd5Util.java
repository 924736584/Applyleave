package com.Yfun.interview.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName : StrToMd5Util
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-08-28 21:21
 */
public class StrToMd5Util {

    public static String toMd5(String plainText) {
    byte[] secretBytes = null;
        try {
        secretBytes = MessageDigest.getInstance("md5").digest(
                plainText.getBytes());
    } catch (
    NoSuchAlgorithmException e) {
        throw new RuntimeException("没有这个md5算法！");
    }
    String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
        md5code = "0" + md5code;
    }
        return md5code;
}
}
