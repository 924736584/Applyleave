package com.Yfun.interview.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @ClassName : ReadPerportiesResource
 * @Description : 读取Perporties文件资源
 * @Author : DeYuan
 * @Date: 2020-08-28 17:57
 */
public class ReadPropertiesResourceUtil {
    private Properties properties;
    public ReadPropertiesResourceUtil() throws FileNotFoundException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/GlobalConfiguration.properties");
        if(resourceAsStream==null){
            throw new FileNotFoundException("文件未找到请查看resource目录下是否存在全局配置文件");
        }
        properties=new Properties();
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                resourceAsStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    public Properties getProperties(){
        return properties;
    }
}
