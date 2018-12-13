package com.dubbohelper.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Setter
@Getter
@Builder
public class MavenCoordDTO implements Comparable<MavenCoordDTO> {

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

    @Override
    public int compareTo(MavenCoordDTO o) {
        if (hasEmpty(applicationName, groupId, artifactId, version)
                || hasEmpty(o.getApplicationName(), o.getGroupId(), o.getArtifactId(), o.getVersion())) {
            return -1;
        }
        if (applicationName.equals(o.getApplicationName())
                && groupId.equals(o.getGroupId())
                && artifactId.equals(o.getArtifactId())
                && version.equals(o.getVersion())) {
            return 0;
        }
        return -1;
    }

    private boolean hasEmpty(String... strs) {
        for (String str : strs) {
            if (StringUtils.isEmpty(str)) {
                return true;
            }
        }
        return false;
    }
}
