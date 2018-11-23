package com.dubbohelper.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by zhangxiaoman on 2018/11/14.
 */
@Getter
@Setter
@Builder
public class Application implements Serializable, Comparable<Application> {
    /**
     * 应用名
     */
    String application;

    String groupId;

    String artifactId;

    final Set<Version> versions = new TreeSet<>();

    /**
     * 负责人
     */
    String owner;

    /**
     * zk节点路径-方便快速定位
     */
    String path;

    @Override
    public int compareTo(Application o) {
//        if(o.getApplication().equals(application))
        return 0;
    }
}
