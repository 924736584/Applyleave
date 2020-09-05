package com.Yfun.interview.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

/**
 * @ClassName : ObtainMonitorHandler
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-01 20:27
 */
@Component
@Aspect
public class ObtainMonitorHandler {

    @Pointcut("@annotation(com.Yfun.interview.annotation.Obtain)")
    private void pointcut(){};

    @Around("pointcut() && @annotation(obtain)")
    public Object monitor(ProceedingJoinPoint joinPoint, Obtain obtain){
        Class<?> aClass = joinPoint.getSignature().getDeclaringType();
        Obtain config = aClass.getAnnotation(Obtain.class);
        if(config!=null){
            String fileName = config.file_name();
            if (!fileName.startsWith(File.separator)) {
                fileName = File.separator + fileName;
            }
            URL url = aClass.getResource(fileName);
            Properties props = new Properties();
            try {
                props.load(url.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<String> propertyNames = props.stringPropertyNames();
            Field[] fields = aClass.getDeclaredFields();
            ObjectMapper mapper = new ObjectMapper();
            Arrays.stream(fields).forEach(field -> {
                if(field.isAnnotationPresent(AutoData.class)){
                    field.setAccessible(true);
                    if (field.getType() != String.class) {
                        //通过Json转换
                        String propertiesName=config.prefix()+"."+field.getName();
                        try {
                            String property = props.getProperty(propertiesName);
                            if(StringUtils.isNotBlank(property)) {
                                field.set(aClass, mapper.readValue(property, field.getType()));
                            }
                        } catch (IllegalAccessException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        Object proceed=null;
        try {
             proceed = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return proceed;
    }

    public Object prcess(Object target, Definition definition) {
        Class<?> clazz = definition.getClass();
        Obtain  config = clazz.getAnnotation(Obtain .class);
        if (config == null) {
            return target;
        }
        String fileName = config.file_name();
        if (!fileName.startsWith(File.separator)) {
            fileName = File.separator + fileName;
        }

        URL url = clazz.getResource(fileName);
        Properties props = new Properties();
        try {
            props.load(url.openStream());
            Set<String> keys = props.stringPropertyNames();
            for (String propName : keys) {
                String fieldName = propName.replace(config.prefix() + ".", "");
                try {
                    //按照properties的key，去掉前缀后读取类的字段Field
                    Field field = clazz.getDeclaredField(fieldName);
                    // 开启操作权限
                    field.setAccessible(true);
                    // 字段不是string型就需要转换一下
                    ObjectMapper mapper = new ObjectMapper();
                    if (field.getType() != String.class) {
                        //通过Json转换
                      field.set(target,mapper.readValue(props.getProperty(propName),field.getType()));
                    }
                } catch (Exception e) {
                    // 注入失败也无所谓，无视这个字段下一个注入
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return target;
    }
}
