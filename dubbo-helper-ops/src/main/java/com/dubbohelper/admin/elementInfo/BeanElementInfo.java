package com.dubbohelper.admin.elementInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class BeanElementInfo implements ElementInfo {
    /**
     * 字段名称
     */
    String name;
    /**
     * 要素类型
     */
    final String type = "bean";
    /**
     * 字段描述
     */
    String desc;
    /**
     * 版本
     */
    String version;
    /**
     * 元素
     */
    final List<ElementInfo> elements = new ArrayList<ElementInfo>();
}
