package com.dubbohelper.admin.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mr.zhang  2018-11-22 17:18
 */
@Setter
@Getter
public class MavenDataDTO {

    private String groupId;

    private String artifactId;

    private String version;

    private String timestamp;

    private String buildNumber;

    private String lastUpdated;

    private String extension;

    private String value;

    private String updated;

    private String classifier;
}
