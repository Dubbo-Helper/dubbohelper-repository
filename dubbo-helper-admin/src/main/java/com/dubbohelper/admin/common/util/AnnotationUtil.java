package com.dubbohelper.admin.common.util;

import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengcy on 2019/1/27.
 */
public class AnnotationUtil {
    public static Annotation getAnnotation(Annotation[] annotations,String annotationName) {
        Annotation annotation = null ;
        if(annotations==null||annotations.length<=0){
            return annotation;
        }

        for(Annotation an:annotations){
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(an);
            String s=invocationHandler.toString();
            String s1 = invocationHandler.getClass().getTypeName();
            Class<?> aClass=null;
            try {
                Field memberValuesField = invocationHandler.getClass().getDeclaredField("type");
                memberValuesField.setAccessible(true);
                 aClass =  (Class) memberValuesField.get(invocationHandler);

            } catch (Exception e) {
                e.printStackTrace();
            }

            String name = aClass.getName();
            if(name.endsWith(annotationName)){
                return an;
            }
        }

        return annotation;
    }

    public static Object getAnnotationMemberValue(Annotation annotation,String fieldName) {
        Map<String,Object> memberValues = getAnnotationAttributes(annotation);
        if(memberValues==null|| !memberValues.containsKey(fieldName)){
            return null;
        }

        return memberValues.get(fieldName);
    }

    public static Map<String,Object> getAnnotationMemberValues(Annotation annotation)throws Exception {
        Map<String,Object> memberValues = new HashMap<>();

        InvocationHandler ih = Proxy.getInvocationHandler(annotation);
        Field memberValuesField = ih.getClass().getDeclaredField("memberValues");
        memberValuesField.setAccessible(true);
        memberValues = (Map) memberValuesField.get(ih);

        return memberValues;
    }

    public static Map<String, Object> getAnnotationAttributes(Annotation annotation) {
        return AnnotationUtils.getAnnotationAttributes(annotation);
    }
}
