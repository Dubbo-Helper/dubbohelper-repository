package com.dubbohelper.admin.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mr.zhang  2018-11-22 17:18
 */
@Setter
@Getter
public class MavenDataDTO {

    String groupId;
    String artifactId;
    String version;
    String timestamp;
    String buildNumber;
    String lastUpdated;
    String extension;
    String value;
    String updated;
    String classifier;
}
