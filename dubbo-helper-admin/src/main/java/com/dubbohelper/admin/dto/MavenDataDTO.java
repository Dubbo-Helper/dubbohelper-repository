package com.dubbohelper.admin.dto;

import com.dubbohelper.admin.common.enums.FilePathEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Mr.zhang  2018-11-22 17:18
 */
@Setter
@Getter
public class MavenDataDTO {

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
    /**
     * 远程maven仓库的URL地址，默认使用bw30的远程maven-public库
     */
    private String repository = "";
    /**
     * 下载的jar包存放的目标地址，默认为./target/repo
     */
    private String target = FilePathEnum.JARPATH.getAbsolutePath();
    /**
     * 登录远程maven仓库的用户名，若远程仓库不需要权限，设为null，默认为null
     */
    private String username = null;
    /**
     * 登录远程maven仓库的密码，若远程仓库不需要权限，设为null，默认为null
     */
    private String password = null;


    public MavenDataDTO() {
        super();
    }

    public MavenDataDTO(MavenCoordDTO dto) {
        this.groupId = dto.getGroupId();
        this.artifactId = dto.getArtifactId();
        this.version = dto.getVersion();
    }
}
