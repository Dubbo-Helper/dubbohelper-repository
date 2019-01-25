package com.dubbohelper.admin.controller;

import com.dubbohelper.admin.dto.MavenCoordDTO;
import com.dubbohelper.admin.scanner.ApiDocScanner;
import com.dubbohelper.admin.scanner.InterfaceInfo;
import com.dubbohelper.admin.scanner.ServiceInfo;
import com.dubbohelper.admin.service.ApiDocService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/apiDoc")
public class ApiDocController {
    
    @Autowired
    private ApiDocScanner apiDocScanner;
    @Autowired
    private ApiDocService apiDocService;

    @RequestMapping("/service")
    @ResponseBody
    public Map<String ,Object>  listServices(@RequestBody MavenCoordDTO dto) throws Exception {
        Map<String ,Object> map = new HashedMap();
        List<ServiceInfo> serviceList = apiDocService.listService(dto);

        InterfaceInfo currentInterfaceInfo = new InterfaceInfo();
        map.put("packageName", dto.getArtifactId());
        map.put("serviceList",serviceList);
        map.put("currentInterfaceInfo",currentInterfaceInfo);

        return map;
    }

    @RequestMapping("/method")
    @ResponseBody
    public Map<String ,Object>  listInterfaces(@RequestBody MavenCoordDTO dto, String service) throws Exception {
        Map<String ,Object> map = new HashedMap();
        List<ServiceInfo> serviceList = apiDocService.listService(dto);
        List<InterfaceInfo> interfaceList = apiDocService.listInterface(dto, service);

        InterfaceInfo currentInterfaceInfo = new InterfaceInfo();
        currentInterfaceInfo.setClassName(service);
        map.put("packageName", dto.getArtifactId());
        map.put("serviceList",serviceList);
        map.put("interfaceList",interfaceList);
        map.put("currentInterfaceInfo",currentInterfaceInfo);

        return map;
    }

    @RequestMapping("/document")
    @ResponseBody
    public Map<String ,Object> listInterface(@RequestBody MavenCoordDTO dto, String service, String method) throws Exception {
        Map<String ,Object> map = new HashedMap();
        List<ServiceInfo> serviceList = apiDocService.listService(dto);
        List<InterfaceInfo> interfaceList = apiDocService.listInterface(dto, service);
        InterfaceInfo interfaceInfo = apiDocService.interfaceDetail(dto, service, method);

        map.put("packageName", dto.getArtifactId());
        map.put("serviceList",serviceList);
        map.put("interfaceList",interfaceList);
        map.put("interfaceInfo",interfaceInfo);

        return map;
    }

    @RequestMapping("downloadApiDoc")
    public void downloadApiDoc(HttpServletResponse response, MavenCoordDTO dto) {
        OutputStream outputStream = null;
        String projectName = "test";
        try {
            outputStream = response.getOutputStream();
            response.reset();
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + projectName + ".md");
            apiDocService.downloadApiDoc(dto, projectName, outputStream);
        } catch (FileNotFoundException e) {
            log.error("文件[" + projectName + "]不存在", e);
            response.setStatus(404);
        } catch (Exception e) {
            log.error("文件[" + projectName + "]下载失败", e);
            response.setStatus(500);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                response.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
