package com.dubbohelper.admin.service;

import com.dubbohelper.admin.dto.ConfigureDTO;

public interface ConfigureService {

    /**
     * 修改系统配置
     *
     * @param dto 配置参数
     * @return 修改结果
     */
    boolean updateConfigures(ConfigureDTO dto);

    /**
     * 获取系统配置
     *
     * @return 系统配置
     */
    ConfigureDTO getConfigures();
}
