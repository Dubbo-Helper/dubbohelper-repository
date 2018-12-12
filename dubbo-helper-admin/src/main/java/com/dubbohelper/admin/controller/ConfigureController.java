package com.dubbohelper.admin.controller;

import com.dubbohelper.admin.dto.ConfigureDTO;
import com.dubbohelper.admin.service.ConfigureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/config")
public class ConfigureController {

    @Autowired
    private ConfigureService configureService;

    /**
     * 修改系统配置
     *
     * @param dto 配置参数
     * @return 修改结果
     */
    @RequestMapping("/update")
    public boolean updateConfigures(@RequestBody @Valid ConfigureDTO dto) {
        return configureService.updateConfigures(dto);
    }

    /**
     * 获取系统配置
     *
     * @return 系统配置
     */
    @RequestMapping("/get")
    public ConfigureDTO getConfigures() {
        return configureService.getConfigures();
    }
}
