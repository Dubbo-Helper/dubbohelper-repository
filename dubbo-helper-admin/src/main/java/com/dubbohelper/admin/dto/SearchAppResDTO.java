package com.dubbohelper.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by zhangxiaoman on 2018/12/10.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
public class SearchAppResDTO {
    /**
     * 项目名
     */
    String appName;

    /**
     * 坐标1
     */
    String groupId;

    /**
     * 坐标2
     */
    String artifactId;
}
