package com.dubbohelper.admin.service.impl;

import com.dubbohelper.admin.dto.Application;
import com.dubbohelper.admin.dto.SearchAppResDTO;
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
    public List<SearchAppResDTO> search(String keyWord) {
        List<SearchAppResDTO> list = new ArrayList<>();
        if (!StringUtils.isEmpty(keyWord)) {//全量搜索只展示前10条
            for (Map.Entry<String, Application> entry : registerServiceSync.registryApplicationMap.entrySet()) {
                String appName = entry.getKey();
                if (appName.contains(keyWord)) {
                    Application app = entry.getValue();
                    SearchAppResDTO searchAppResDTO = SearchAppResDTO.builder()
                            .appName(app.getApplication())
                            .groupId(app.getGroupId())
                            .artifactId(app.getArtifactId())
                            .build();
                    list.add(searchAppResDTO);
                }
            }
        }
        //TODO  本地缓存jar包解析项目
        return list;
    }

}
