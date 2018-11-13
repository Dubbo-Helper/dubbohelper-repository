package com.dubbohelper.admin.apidoc;

import com.dubbohelper.admin.elementInfo.ElementInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceInfo implements Comparable<InterfaceInfo> {
    /**
     * 接口名称
     */
    String name;
    /**
     * 接口描述
     */
    String desc;
    /**
     * 用法
     */
    String usage;
    /**
     * 接口类
     */
    String className;
    /**
     * 方法对象
     */
    String methodName;
    /**
     * 请求类
     */
    String requestName;
    /**
     * 应答类
     */
    String responseName;
    /**
     * 请求
     */
    final List<ElementInfo> request = new ArrayList<ElementInfo>();
    /**
     * 应答
     */
    final List<ElementInfo> response = new ArrayList<ElementInfo>();

    public int compareTo(InterfaceInfo o) {
        if(name == null && o != null && o.name != null){
            return 1;
        }
        if(name != null && o == null){
            return -1;
        }
        return name.compareTo(o.name);
    }
}
