package com.dubbohelper.admin.dto;

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
    private String repository = "http://maven.zhubajie.la/repository/public/";
    /**
     * 下载的jar包存放的目标地址，默认为./target/repo
     */
    private String target = "temp";
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


    public MavenDataDTO(String groupId, String artifactId) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public MavenDataDTO(String groupId, String artifactId, String version,
                        String repository, /*String target,*/ String username, String password) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.repository = repository;
        /*this.target = target;*/
        this.username = username;
        this.password = password;
    }

    public MavenDataDTO(String groupId, String artifactId, String version,
                        String username, String password) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.username = username;
        this.password = password;
    }
}
