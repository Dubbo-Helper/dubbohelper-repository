package com.dubbohelper.admin.controller;

import com.dubbohelper.admin.dto.MavenCoordDTO;
import com.dubbohelper.admin.scanner.ApiDocScanner;
import com.dubbohelper.admin.scanner.InterfaceInfo;
import com.dubbohelper.admin.scanner.ServiceInfo;
import com.dubbohelper.admin.service.ApiDocService;
import com.dubbohelper.admin.common.util.ModelUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/apiDoc")
public class ApiDocController {
    
    @Autowired
    private ApiDocScanner apiDocScanner;
    @Autowired
    private ApiDocService apiDocService;

    @RequestMapping("/service")
    public ModelAndView listServices(@RequestBody MavenCoordDTO dto) throws Exception {
        List<ServiceInfo> serviceList = apiDocService.listService(dto);

        new Gson().toJson(serviceList);
        ModelAndView modelAndView = new ModelAndView("apiDoc");
        modelAndView.addObject("packageName", dto.getArtifactId());
        InterfaceInfo currentInterfaceInfo = new InterfaceInfo();
        ModelUtil.getModel(modelAndView, serviceList, null, currentInterfaceInfo, null, null);

        return modelAndView;
    }

    @RequestMapping("/method")
    public ModelAndView listInterfaces(@RequestBody MavenCoordDTO dto, String service) throws Exception {
        List<ServiceInfo> serviceList = apiDocService.listService(dto);
        List<InterfaceInfo> interfaceList = apiDocService.listInterface(dto, service);

        ModelAndView modelAndView = new ModelAndView("apiDoc");
        modelAndView.addObject("packageName", dto.getArtifactId());
        InterfaceInfo currentInterfaceInfo = new InterfaceInfo();
        currentInterfaceInfo.setClassName(service);
        ModelUtil.getModel(modelAndView, serviceList, interfaceList, currentInterfaceInfo, null, null);

        return modelAndView;
    }

    @RequestMapping("/document")
    public ModelAndView listInterface(@RequestBody MavenCoordDTO dto, String service, String method) throws Exception {

        List<ServiceInfo> serviceList = apiDocService.listService(dto);
        List<InterfaceInfo> interfaceList = apiDocService.listInterface(dto, service);
        InterfaceInfo interfaceInfo = apiDocService.interfaceDetail(dto, service, method);

        ModelAndView modelAndView = new ModelAndView("apiDoc");
        modelAndView.addObject("packageName", dto.getArtifactId());
        ModelUtil.getModel(modelAndView, serviceList, interfaceList, interfaceInfo, interfaceInfo.getRequest(), interfaceInfo.getResponse());

        return modelAndView;
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
