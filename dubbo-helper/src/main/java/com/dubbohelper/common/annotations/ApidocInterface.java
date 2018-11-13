package com.dubbohelper.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记接口方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ApidocInterface {
    /**
     * 方法描述
     */
    String value() default "";

    /**
     * 用法
     */
    String usage() default "";

    /**
     * 版本
     */
    String since() default "";
}
