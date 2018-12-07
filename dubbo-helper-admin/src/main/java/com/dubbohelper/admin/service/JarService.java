package com.dubbohelper.admin.service;

import com.dubbohelper.admin.dto.MavenCoordDTO;

import java.util.List;

public interface JarService {

    /**
     * 搜索项目
     *
     * @param artifactId artifactId
     * @return 项目列表
     */
    List<MavenCoordDTO> searchApplication(String artifactId);

    /**
     * 搜索jar包列表
     *
     * @param groupId groupId
     * @param artifactId artifactId
     * @return jar包列表
     */
    List<MavenCoordDTO> getJars(String groupId, String artifactId);

    /**
     * 添加或更新jar包
     *
     * @param dto jar maven坐标信息
     * @return 处理结果
     */
    boolean insertOrUpdateJar(MavenCoordDTO dto);
}
