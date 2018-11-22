package com.dubbohelper.admin.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Maven坐标DTO
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
public class MavenCoordinateDTO {

    private String groupId;

    private String artifactId;

    private String version;
}
