package com.dubbohelper.admin.scanner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo implements Comparable<ServiceInfo>{
    /**
     * 接口类描述
     */
    String desc;
    /**
     * 接口类
     */
    String className;
    /**
     * 用法
     */
    String usage;

    public int compareTo(ServiceInfo o) {
        if(className == null && o != null && o.className != null){
            return 1;
        }
        if(className != null && o == null){
            return -1;
        }
        return className.compareTo(o.className);
    }
}
