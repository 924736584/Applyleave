package com.Yfun.interview.util;

/**
 * @ClassName : NickNameCreateUtil
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-08-29 13:28
 */
public class NickNameCreateUtil {
    /**
     * 创建一个随机的用户名算法
     * @return
     */
    public String create(){
        long timeMillis = System.currentTimeMillis();
        long nickName=timeMillis-(timeMillis-19981214)/2;
        return String.valueOf(nickName);
    }
}
