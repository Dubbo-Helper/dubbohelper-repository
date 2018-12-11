package com.dubbohelper.admin.service.impl;

import com.dubbohelper.admin.common.enums.FilePathEnum;
import com.dubbohelper.admin.common.util.FileUtil;
import com.dubbohelper.admin.dto.ConfigureDTO;
import com.dubbohelper.admin.service.ConfigureService;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
public class ConfigureServiceImpl implements ConfigureService {

    private static Map<String, String> configureMap = Maps.newConcurrentMap();

    @Override
    public boolean updateConfigures(ConfigureDTO dto) {

        if (StringUtils.isNotEmpty(dto.getZkAddress())) {
            configureMap.put("zkAddress", dto.getZkAddress());
        }
        if (StringUtils.isNotEmpty(dto.getRepositoryPath())) {
            configureMap.put("repositoryPath", dto.getRepositoryPath());
        }
        if (StringUtils.isNotEmpty(dto.getFileRootPath())) {
            configureMap.put("fileRootPath", dto.getFileRootPath());
        } else {
            configureMap.put("fileRootPath", System.getProperty("user.dir"));
        }

        return FileUtil.createFile(FilePathEnum.CONFIGURE.getPath(), new Gson().toJson(configureMap));
    }

    @Override
    public ConfigureDTO getConfigures() {
        ConfigureDTO configureDTO = new ConfigureDTO();

        if (StringUtils.isEmpty(configureMap.get("zkAddress"))
                || StringUtils.isEmpty(configureMap.get("repositoryPath"))
                || StringUtils.isEmpty(configureMap.get("fileRootPath"))) {
            String configure = FileUtil.readFileByString(FilePathEnum.CONFIGURE.getPath());
            if (StringUtils.isNotEmpty(configure)) {
                Map<String, String> map = new Gson().fromJson(configure, Map.class);
                configureMap.put("zkAddress", map.get("zkAddress"));
                configureMap.put("repositoryPath", map.get("repositoryPath"));
                configureMap.put("fileRootPath", map.get("fileRootPath"));
            }
        }

        configureDTO.setZkAddress(configureMap.get("zkAddress"));
        configureDTO.setRepositoryPath(configureMap.get("repositoryPath"));
        configureDTO.setFileRootPath(configureMap.get("fileRootPath"));

        return configureDTO;
    }
}
