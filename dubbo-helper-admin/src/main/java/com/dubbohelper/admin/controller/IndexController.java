package com.dubbohelper.admin.controller;

import com.dubbohelper.admin.dto.MavenCoordDTO;
import com.dubbohelper.admin.service.JarService;
import com.dubbohelper.admin.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/index")
public class IndexController implements InitializingBean {

    @Autowired
    private JarService jarService;
    @Autowired
    private RegisterService registerService;

    @RequestMapping("/search")
    public Set<MavenCoordDTO> searchApplication(String artifactId) {
        Set<MavenCoordDTO> result = new HashSet<>();

        Set<MavenCoordDTO> local = jarService.search(artifactId);
        Set<MavenCoordDTO> zk = registerService.search(artifactId);
        result.addAll(local);
        result.addAll(zk);

        return result;
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
