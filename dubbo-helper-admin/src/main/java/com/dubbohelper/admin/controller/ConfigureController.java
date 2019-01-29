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
     * 获取系统配置
     *
     * @return 系统配置
     */
    @RequestMapping("/get")
    public ConfigureDTO getConfigures() {
        ConfigureDTO dto =  configureService.getConfigures();
        if(dto==null){
            dto = new ConfigureDTO();
        }

        return dto;
    }

    /**
     * 修改系统配置
     *
     * @param dto 配置参数
     * @return 修改结果
     */
    @RequestMapping("/update")
    public String updateConfigures(@RequestBody @Valid ConfigureDTO dto) {
        boolean result = configureService.updateConfigures(dto);
        String message = "保存失败";
        if(result){
            message = "保存成功";
        }

        return message;
    }
}
