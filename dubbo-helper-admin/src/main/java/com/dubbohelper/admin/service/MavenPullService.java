package com.dubbohelper.admin.service;

import com.dubbohelper.admin.dto.MavenDataDTO;

/**
 * @Author Mr.zhang  2018-11-26 14:08
 */
public interface MavenPullService {

    /***
     *拉取jar包
     * @param dto api jar包maven坐标
     */
    void pullApiJar(MavenDataDTO dto);
}
