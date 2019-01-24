package com.dubbohelper.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaoman on 2018/11/16.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Version implements Comparable<Version> {

    /**
     * pom版本
     */
    String version;

    /**
     * provider版本列表
     * 默认版本为空
     */
    final List<String> defaultVersions = new ArrayList<>();

    @Override
    public int compareTo(Version o) {
       if(o.getVersion().equals(version))
           return 0;
        return -1;
    }
}
