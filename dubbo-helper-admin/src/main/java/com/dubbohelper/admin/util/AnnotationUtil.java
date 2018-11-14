package com.dubbohelper.admin.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析注解工具
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Slf4j
public class AnnotationUtil {

    /**
     * 解析注解
     * @param annotation 注解
     * @return
     */
    public static Map<String,String> getAnnotationDetail(Annotation annotation) {
        Map memberValues = new HashMap<>(16);
        try {
            InvocationHandler ih = Proxy.getInvocationHandler(annotation);
            Field memberValuesField = ih.getClass().getDeclaredField("memberValues");
            memberValuesField.setAccessible(true);
            memberValues = (Map) memberValuesField.get(ih);
        } catch (Exception e) {
            log.error("解析注解失败:{}",annotation.toString(), e);
        }

        return memberValues;
    }
}
