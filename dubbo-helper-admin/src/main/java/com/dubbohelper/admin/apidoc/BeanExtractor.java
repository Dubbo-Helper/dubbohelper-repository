package com.dubbohelper.admin.apidoc;

import com.dubbohelper.admin.common.AnnotationUtil;
import com.dubbohelper.admin.elementInfo.BeanElementInfo;
import com.dubbohelper.admin.elementInfo.ElementInfo;
import com.dubbohelper.admin.elementInfo.ListElementInfo;
import com.dubbohelper.admin.elementInfo.ValueElementInfo;
import com.dubbohelper.common.annotations.ApidocElement;
import com.dubbohelper.common.enums.EnumIntegerCode;
import com.dubbohelper.common.enums.EnumStringCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class BeanExtractor {

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
        Field[] fields = fieldList.toArray(new Field[fieldList.size()]);

        for (Field field : fields) {
            Annotation[] fieldAnnotations = field.getAnnotations();
            if (fieldAnnotations.length > 0) {
                for (Annotation annotation:fieldAnnotations) {
                    if (annotation.annotationType().getSimpleName().equals("ApidocElement")) {
                        Map<String,String> fieldAnnotationMap = AnnotationUtil.getAnnotationDetail(annotation);
                    }
                }
            }
            ApidocElement element = field.getAnnotation(ApidocElement.class);
            String desc = "";
            String version = "";
            if(element != null){
                desc = element.value();
                version = "1.0";
            }
            ElementInfo elementInfo = null;
            if (field.getType().isEnum()) {
                ValueElementInfo valueElementInfo = null;

                if(element != null){
                    valueElementInfo = new ValueElementInfo(field.getName(), desc,"", field.getType().getSimpleName(), element.required(), element.minLen(), element.maxLen(), element.defVal());
                }else{
                    valueElementInfo = new ValueElementInfo(field.getName(), desc, "",field.getType().getSimpleName(), true, 0, 0, "");
                }
                elementInfo = valueElementInfo;
            }else if (field.getType().getName().startsWith("com.dubbo.helper")) {
                if (!Serializable.class.isAssignableFrom(field.getType())) {
                    log.warn("{}.{} 未实现序列化", clazz.getName(), field.getName());
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
                elementInfo = listElementInfo;
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
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
