package com.dubbohelper.admin.service;

public interface JarService {

    /**
     * 搜索项目
     * @param artifactId
     */
    void searchApplication(String artifactId);

    /**
     * 搜索jar包列表
     * @param groupId
     * @param artifactId
     */
    void getJars(String groupId, String artifactId);

    /**
     * 添加jar包
     * @param groupId
     * @param artifactId
     * @param version
     * @param scanPackages
     * @return
     */
    boolean insertJar(String groupId, String artifactId, String version, String scanPackages);
}
