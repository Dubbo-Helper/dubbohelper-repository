package com.dubbohelper.admin.service;

/**
 * @Author Mr.zhang  2018-11-26 14:08
 */
public interface MavenPullService {

    /***
     *拉取jar包
     * @param groupId
     * @param artifactId
     * @param version
     */
    void pull(String groupId,String artifactId,String version);
}
