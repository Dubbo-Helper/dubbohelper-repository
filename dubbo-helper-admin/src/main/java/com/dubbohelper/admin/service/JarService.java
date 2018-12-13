package com.dubbohelper.admin.service;

import com.dubbohelper.admin.dto.MavenCoordDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface JarService {

    /**
     * 搜索项目
     *
     * @param artifactId artifactId
     * @return 项目列表
     */
    Set<MavenCoordDTO> search(String artifactId);

    /**
     * 添加或更新jar包
     *
     * @param dto jar maven坐标信息
     * @return 处理结果
     */
    boolean insertOrUpdateJar(MavenCoordDTO dto);

    /**
     * 获取Jar在仓库中的所有版本
     *
     * @param dto jar maven坐标信息
     * @return
     */
    List<String> getJarVersions(MavenCoordDTO dto);
}
