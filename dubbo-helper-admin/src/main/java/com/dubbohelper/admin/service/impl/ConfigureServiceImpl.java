package com.dubbohelper.admin.service.impl;

import com.dubbohelper.admin.common.enums.FilePathEnum;
import com.dubbohelper.admin.common.util.FileUtil;
import com.dubbohelper.admin.dto.ConfigureDTO;
import com.dubbohelper.admin.service.ConfigureService;
import com.dubbohelper.admin.service.RegisterService;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConfigureServiceImpl implements ConfigureService {

    private static Map<String, String> configureMap = Maps.newConcurrentMap();
    @Autowired
    private RegisterService registerService;
    @Override
    public boolean updateConfigures(ConfigureDTO dto) {

        if (StringUtils.isNotEmpty(dto.getZkAddress())) {
            configureMap.put("zkAddress", dto.getZkAddress());
            try {
                //重新连接zk
                registerService.reConnection(configureMap.get("zkAddress"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isNotEmpty(dto.getRepositoryUrl())) {
            configureMap.put("repositoryUrl", dto.getRepositoryUrl());
        }

        return FileUtil.createFile(FilePathEnum.CONFIGURE.getAbsolutePath(), new Gson().toJson(configureMap));
    }

    @Override
    public ConfigureDTO getConfigures() {
        ConfigureDTO configureDTO = new ConfigureDTO();

        if (StringUtils.isEmpty(configureMap.get("zkAddress"))
                || StringUtils.isEmpty(configureMap.get("repositoryUrl"))) {
            String configure = FileUtil.readFileByString(FilePathEnum.CONFIGURE.getAbsolutePath());
            if(null==configure){//创建一个默认的配置文件
//                configureDTO.setZkAddress("dubbo.helper.zk.address:2181");
//                configureDTO.setRepositoryUrl("http://nexus.test.com/repository/public/");
//                boolean initConfigFileResult=updateConfigures(configureDTO);
//                if(initConfigFileResult){
//                    configure = FileUtil.readFileByString(FilePathEnum.CONFIGURE.getAbsolutePath());
//                }
                return null;
            }

            if (StringUtils.isNotEmpty(configure)) {
                Map<String, String> map = new Gson().fromJson(configure, Map.class);
                configureMap.put("zkAddress", map.get("zkAddress"));
                configureMap.put("repositoryUrl", map.get("repositoryUrl"));
            }
        }

        configureDTO.setZkAddress(configureMap.get("zkAddress"));
        configureDTO.setRepositoryUrl(configureMap.get("repositoryUrl"));

        return configureDTO;
    }
}
