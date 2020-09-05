package com.Yfun.interview.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @ClassName : ObjectToStringUtil
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-08-30 12:13
 */
public class ObjectToStringUtil {
    private static ObjectMapper mapper=new ObjectMapper();
    public static String changeString(Object object){
        String result="";
        try {
            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
