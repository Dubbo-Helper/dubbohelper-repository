package com.dubbohelper.admin.dto;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class SearchAppResDTO {
    /**
     * 项目名
     */
    String applicationName;

    /**
     * 坐标1
     */
    String groupId;

    /**
     * 坐标2
     */
    String artifactId;
}
