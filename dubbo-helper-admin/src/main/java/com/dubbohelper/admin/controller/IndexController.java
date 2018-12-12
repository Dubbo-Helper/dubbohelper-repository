package com.dubbohelper.admin.controller;

import com.dubbohelper.admin.dto.MavenCoordDTO;
import com.dubbohelper.admin.service.JarService;
import com.dubbohelper.admin.service.RegisterService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/index")
public class IndexController implements InitializingBean {

    @Autowired
    private JarService jarService;
    @Autowired
    private RegisterService registerService;

    @RequestMapping("/search")
    public List<MavenCoordDTO> searchApplication(String artifactId) {

        Map<String, MavenCoordDTO> applications = new HashMap<>();
        applications.putAll(jarService.searchApplication(artifactId));

        return new ArrayList<>(applications.values());
    }


    @RequestMapping("/getJars")
    public String getJars(@RequestBody @Valid MavenCoordDTO dto) {
        List<MavenCoordDTO> result = jarService.getJars(dto.getGroupId(), dto.getArtifactId());

        return new Gson().toJson(result);
    }

    @RequestMapping("/insertOrUpdateJar")
    public boolean insertOrUpdateJar(@RequestBody @Valid MavenCoordDTO dto) {
        return jarService.insertOrUpdateJar(dto);
    }

    @RequestMapping("/getJarVersions")
    public List<String> getJarVersions(@RequestBody @Valid MavenCoordDTO dto) {
        return jarService.getJarVersions(dto);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //加载文件内容
    }
}
