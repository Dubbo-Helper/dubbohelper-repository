package com.dubbohelper.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaoman on 2018/11/16.
 */
@Getter
@Setter
@Builder
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
