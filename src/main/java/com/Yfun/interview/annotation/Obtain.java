package com.Yfun.interview.annotation;

import java.lang.annotation.*;

/**
 * @ClassName : obtain
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-01 16:46
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Obtain {
    /**
     * 默认文件为resource下的application.properties文件
     * @return
     */
    String file_name() default "application";
    String prefix();
}
