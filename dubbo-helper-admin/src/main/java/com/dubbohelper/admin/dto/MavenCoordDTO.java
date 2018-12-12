package com.dubbohelper.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MavenCoordDTO {

    /**
     * 应用名
     */
    private String applicationName;
    /**
     * jar包在maven仓库中的groupId
     */
    private String groupId;
    /**
     * jar包在maven仓库中的artifactId
     */
    private String artifactId;
    /**
     * jar包在maven仓库中的version
     */
    private String version;
}
