package com.dubbohelper.admin.service.impl;

import com.dubbohelper.admin.common.config.Config;
import com.dubbohelper.admin.dto.Application;
import com.dubbohelper.admin.dto.MavenCoordDTO;
import com.dubbohelper.admin.service.RegisterService;
import com.dubbohelper.admin.service.sync.RegisterServiceSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangxiaoman on 2018/11/15.
 */
@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private RegisterServiceSync registerServiceSync;
    @Autowired
    private Config config;

    @Override
    public Set<MavenCoordDTO> search(String keyWord) {
        Set<MavenCoordDTO> set = new HashSet<>();
        if (!StringUtils.isEmpty(keyWord)) {
            for (Map.Entry<String, Application> entry : registerServiceSync.registryApplicationMap.entrySet()) {
                String appName = entry.getKey();
                if (appName.contains(keyWord)) {
                    Application app = entry.getValue();
                    MavenCoordDTO mavenCoordDTO = MavenCoordDTO.builder()
                            .applicationName(app.getApplication())
                            .groupId(app.getGroupId())
                            .artifactId(app.getArtifactId())
                            .build();
                    set.add(mavenCoordDTO);
                }
            }
        }
        return set;
    }

    @Override
    public void disconnect() {
        registerServiceSync.destroy();
    }

    @Override
    public void reConnection(String zkUrl) throws Exception {
        config.setDubboUrl(zkUrl);
        registerServiceSync.conn();
        registerServiceSync.initAppList();
        registerServiceSync.listener();
    }
}
