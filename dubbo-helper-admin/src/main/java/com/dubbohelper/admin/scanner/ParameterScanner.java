package com.dubbohelper.admin.scanner;

import com.dubbohelper.admin.scanner.elementInfo.BeanElementInfo;
import com.dubbohelper.admin.scanner.elementInfo.ElementInfo;
import com.dubbohelper.admin.scanner.elementInfo.ListElementInfo;
import com.dubbohelper.admin.scanner.elementInfo.ValueElementInfo;
import com.dubbohelper.admin.util.AnnotationUtil;
import com.dubbohelper.common.annotations.ApidocElement;
import com.dubbohelper.common.enums.EnumIntegerCode;
import com.dubbohelper.common.enums.EnumStringCode;
import com.dubbohelper.common.enums.ResultCodeEnum;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ParameterScanner {

    public List<ElementInfo> extract(Class clazz) {
        return extract0(clazz, 0);
    }

    private List<ElementInfo> extract0(Class clazz, int deep) {
        deep++;
        if (deep > 10) {
            throw new IllegalArgumentException("深度过深，存在循环");
        }
        List<ElementInfo> list = new ArrayList<ElementInfo>();
        List<Field> fieldList = new ArrayList<Field>();
        //获取父类的字段
        Class<?> superClass = clazz.getSuperclass();
        if(superClass != null){
            fieldList.addAll(Arrays.asList(superClass.getDeclaredFields()));
        }
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));

        for (Field field : fieldList) {
            Map<String,String> fieldAnnotationMap = null;
            Annotation[] fieldAnnotations = field.getAnnotations();
            if (fieldAnnotations.length > 0) {
                for (Annotation annotation:fieldAnnotations) {
                    if (annotation.annotationType().getSimpleName().equals("ApidocElement")) {
                        fieldAnnotationMap = AnnotationUtil.getAnnotationDetail(annotation);
                        break;
                    }
                }
            }
            ApidocElement element = field.getAnnotation(ApidocElement.class);
            String desc = "";
            String version = "";
            if(fieldAnnotationMap != null){
                desc = fieldAnnotationMap.get("value");
                version = "1.0";
            }
            ElementInfo elementInfo = null;
            if (field.getType().isEnum()) {
                ValueElementInfo valueElementInfo = null;
                if(fieldAnnotationMap != null){
                    valueElementInfo = new ValueElementInfo(field.getName(), desc,"", field.getType().getSimpleName(),
                            Boolean.valueOf(fieldAnnotationMap.get("required")), Integer.valueOf(fieldAnnotationMap.get("minLen")), Integer.valueOf(fieldAnnotationMap.get("maxLen")), fieldAnnotationMap.get("defVal"));
                }else{
                    valueElementInfo = new ValueElementInfo(field.getName(), desc, "",field.getType().getSimpleName(), true, 0, 0, "");
                }
                elementInfo = valueElementInfo;
            } else if (field.getType().getName().startsWith("com.zbj.finance")) {
                if (!Serializable.class.isAssignableFrom(field.getType())) {
                    log.error("{}.{} 未实现序列化", clazz.getName(), field.getName());
                }
                if(clazz == field.getType()){
                    log.error("{}.{} 存在循环引用的情况", clazz.getName(), field.getName());
                }
                BeanElementInfo beanElementInfo = new BeanElementInfo(field.getName(), desc, version);
                List<ElementInfo> elements = extract(field.getType());
                beanElementInfo.getElements().addAll(elements);
                elementInfo = beanElementInfo;
            } else if(field.getType() == List.class){
                ListElementInfo listElementInfo = new ListElementInfo(field.getName(), desc, version);
                Type type = field.getGenericType();
                //检查泛型
                if (type instanceof ParameterizedType) {
                    //获取泛型
                    ParameterizedType target = (ParameterizedType) type;
                    Type[] parameters = target.getActualTypeArguments();
                    Class<?> modelClass = (Class<?>) parameters[0];
                    //对多条的数据进行提取
                    listElementInfo.getElements().addAll(extract0(modelClass, deep));
                }
                elementInfo = listElementInfo;
            } else {
                List<String> enums = new ArrayList<String>();
                if (element != null && element.enumClass() != Enum.class) {
                    if (EnumStringCode.class.isAssignableFrom(element.enumClass())
                            || EnumIntegerCode.class.isAssignableFrom(element.enumClass())) {
                        enums = extractEnum(element.enumClass());
                    } else {
                        log.error("{}.{} @ApidocElement(enumClass) 设置的字典枚举无效", clazz.getName(), field.getName());
                    }
                }
                ValueElementInfo valueElementInfo = null;

                if(element != null){
                    valueElementInfo = new ValueElementInfo(field.getName(), desc, "",field.getType().getSimpleName(), element.required(), element.minLen(), element.maxLen(), element.defVal());
                }else{
                    valueElementInfo = new ValueElementInfo(field.getName(), desc, "",field.getType().getSimpleName(), true, 0, 0, "");
                }
                valueElementInfo.getEnumDesc().addAll(enums);
                elementInfo = valueElementInfo;
            }
            list.add(elementInfo);
        }
        return list;
    }

    /**
     * 解析枚举类
     * @param enumClass 枚举类
     */
    private List<String> extractEnum(Class<?> enumClass) {

        List<String> list = new ArrayList<String>();
        Object[] values = enumClass.getEnumConstants();
        Method getCodeMethod = null;
        Method getDescMethod = null;
        try {
            getCodeMethod = enumClass.getMethod("getCode");
            getDescMethod = enumClass.getMethod("getDesc");
        } catch (NoSuchMethodException e) {
            return list;
        }
        if (getCodeMethod != null && getDescMethod != null) {
            for (Object val : values) {
                try {
                    Object code = getCodeMethod.invoke(val);
                    Object desc = getDescMethod.invoke(val);
                    list.add(code + ":" + desc);
                } catch (Exception e) {
                    log.error("解析枚举类失败:{}",enumClass.toString(),e);
                }
            }
        }
        return list;
    }
}
