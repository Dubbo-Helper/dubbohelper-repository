package com.dubbohelper.admin.service.impl;

import com.dubbohelper.admin.dto.Application;
import com.dubbohelper.admin.service.RegisterService;
import com.dubbohelper.admin.service.sync.RegisterServiceSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxiaoman on 2018/11/15.
 */
@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private RegisterServiceSync registerServiceSync;

    @Override
    public List<String> myCollect(String ip) {
        List<String> rst = new ArrayList<>();
        rst.addAll(registerServiceSync.collectApplications.get(ip));
        if(rst.size() == 0){
            //TODO 从cookie中读取
        }
        return rst;
    }

    @Override
    public List<Application> search(String keyWord) {
        List<Application> list = new ArrayList<>();
        if (StringUtils.isEmpty(keyWord)) {//全量搜索只展示前10条
            int i = 0;
            for (Map.Entry<String, Application> entry : registerServiceSync.registryApplicationMap.entrySet()) {
                if (i < 10) {
                        Application app1 = entry.getValue();
                        Application app = Application.builder()
                                .application(app1.getApplication())
                                .groupId(app1.getGroupId())
                                .artifactId(app1.getArtifactId())
                                .owner(app1.getOwner())
                                .build();
                        list.add(app);
                }
                i++;
            }
            return list;
        }
        for (Map.Entry<String, Application> entry : registerServiceSync.registryApplicationMap.entrySet()) {
            String appName = entry.getKey();
            if(appName.contains(keyWord)){
                Application app1 = entry.getValue();
                Application app = Application.builder()
                                .application(app1.getApplication())
                                .groupId(app1.getGroupId())
                                .artifactId(app1.getArtifactId())
                                .owner(app1.getOwner())
                                .build();
                        list.add(app);
            }
        }
        return list;
    }

    @Override
    public void mark(String ip, String applicationName) {
        List<String> list = registerServiceSync.collectApplications.get(ip);
        if (null == list) {
            List<String> apps = new ArrayList<>();
            apps.add(applicationName);
            registerServiceSync.collectApplications.put(ip, apps);
        } else {
            list.add(applicationName);
        }
    }
}
