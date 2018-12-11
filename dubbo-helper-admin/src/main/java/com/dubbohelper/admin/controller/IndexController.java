package com.dubbohelper.admin.controller;

import com.dubbohelper.admin.dto.MavenCoordDTO;
import com.dubbohelper.admin.service.JarService;
import com.dubbohelper.admin.service.RegisterService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/index")
public class IndexController implements InitializingBean {

    @Autowired
    private JarService jarService;
    @Autowired
    private RegisterService registerService;

    @RequestMapping("/searchApplication")
    public String searchApplication(String artifactId) {
        List<MavenCoordDTO> result = new ArrayList<>();

        List<MavenCoordDTO> localList = jarService.searchApplication(artifactId);
        result.addAll(localList);

        List<MavenCoordDTO> zkList = new ArrayList<>();
        result.addAll(zkList);

        return new Gson().toJson(result);
    }


    @RequestMapping("/getJars")
    public String getJars(MavenCoordDTO dto) {
        List<MavenCoordDTO> result = jarService.getJars(dto.getGroupId(),dto.getArtifactId());

        return new Gson().toJson(result);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //加载文件内容
    }
}
