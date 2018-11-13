package com.dubbohelper.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记接口参数
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface ApidocElement {
	/**
	 * 字段描述
	 */
	String value();

	/**
	 * 版本
	 */
	String since() default "";

	/**
	 * 最大长度
	 */
	int maxLen() default 255;

	/**
	 * 最小长度
	 */
	int minLen() default 0;

	/**
	 * 是否必输
	 */
	boolean required() default true;

	/**
	 * 枚举字典类
	 */
	Class enumClass() default Enum.class;

	/**
	 * 缺省值
	 */
	String defVal() default "";
}
