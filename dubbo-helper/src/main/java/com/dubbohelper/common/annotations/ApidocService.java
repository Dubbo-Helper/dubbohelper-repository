package com.dubbohelper.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记接口服务类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ApidocService {
    /**
     * 服务描述
     */
    String value();

    /**
     * 用法
     */
    String usage() default "";

    /**
     * 版本
     */
    String since() default "";
}
