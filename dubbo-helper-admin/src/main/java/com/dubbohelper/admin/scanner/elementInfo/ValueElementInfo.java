package com.dubbohelper.admin.scanner.elementInfo;

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
public class ValueElementInfo implements ElementInfo {
    /**
     * 字段名称
     */
    String name;
    /**
     * 要素类型
     */
    final String type = "value";
    /**
     * 字段描述
     */
    String desc;
    /**
     * 版本
     */
    String version;
    /**
     * 字典描述
     */
    final List<String> enumDesc = new ArrayList<String>();
    /**
     * 数据类型
     */
    String dataType;
    /**
     * 是否必输
     */
    boolean required;
    /**
     * 最小长度
     */
    int minLen;
    /**
     * 最大长度
     */
    int maxLen;
    /**
     * 缺省值
     */
    String defVal;
}
