package com.dubbohelper.admin.controller;

import com.dubbohelper.admin.dto.MavenCoordinateDTO;
import com.dubbohelper.admin.scanner.ApiDocScanner;
import com.dubbohelper.admin.scanner.InterfaceInfo;
import com.dubbohelper.admin.scanner.ServiceInfo;
import com.dubbohelper.admin.service.ApiDocService;
import com.dubbohelper.admin.service.impl.ApiDocServiceImpl;
import com.dubbohelper.admin.util.ModelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
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
public class ApiDocController implements InitializingBean {

    private ApiDocScanner scanner = new ApiDocScanner();

    private ApiDocService apiDocService = new ApiDocServiceImpl();

    @RequestMapping("")
    public ModelAndView listApplication() throws Exception{
        List<String> applicationList  = apiDocService.listApplication();

        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("applicationList",applicationList);

        return modelAndView;
    }

    @RequestMapping("/service")
    public ModelAndView listServices(String packageName) throws Exception{
        List<ServiceInfo> serviceList  = apiDocService.listService(packageName);

        ModelAndView modelAndView = new ModelAndView("apiDoc");
        modelAndView.addObject("packageName",packageName);
        InterfaceInfo currentInterfaceInfo = new InterfaceInfo();
        ModelUtil.getModel(modelAndView,serviceList,null,currentInterfaceInfo,null,null);

        return modelAndView;
    }

    @RequestMapping("/method")
    public ModelAndView listInterfaces(String packageName, String service) throws Exception{
        List<ServiceInfo> serviceList  = apiDocService.listService(packageName);
        List<InterfaceInfo> interfaceList = apiDocService.listInterface(packageName,service);

        ModelAndView modelAndView = new ModelAndView("apiDoc");
        modelAndView.addObject("packageName",packageName);
        InterfaceInfo currentInterfaceInfo = new InterfaceInfo();
        currentInterfaceInfo.setClassName(service);
        ModelUtil.getModel(modelAndView,serviceList,interfaceList,currentInterfaceInfo,null,null);

        return modelAndView;
    }

    @RequestMapping("/document")
    public ModelAndView listInterface(String packageName, String service, String method) throws Exception{

        List<ServiceInfo> serviceList  = apiDocService.listService(packageName);
        List<InterfaceInfo> interfaceList = apiDocService.listInterface(packageName,service);
        InterfaceInfo interfaceInfo = apiDocService.interfaceDetail(packageName,service, method);

        ModelAndView modelAndView = new ModelAndView("apiDoc");
        modelAndView.addObject("packageName",packageName);
        ModelUtil.getModel(modelAndView,serviceList,interfaceList,interfaceInfo, interfaceInfo.getRequest(),interfaceInfo.getResponse());

        return modelAndView;
    }

    @RequestMapping("downloadApiDoc")
    public void downloadApiDoc(HttpServletResponse response, String packages) {
        OutputStream outputStream = null;
        String projectName = "test";
        try{
            outputStream = response.getOutputStream();
            response.reset();
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName="+ projectName +".md");
            apiDocService.downloadApiDoc(packages,projectName,outputStream);
        } catch  (FileNotFoundException e){
            log.error("文件[" + projectName + "]不存在",e);
            response.setStatus(404);
        }catch (Exception e) {
            log.error("文件[" + projectName + "]下载失败",e);
            response.setStatus(500);
        }finally{
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

    @RequestMapping("/loadApplication")
    public void loadApplication(){

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        String[] docScanPackage = {"com.dubbohelper.test.api.service"};
        MavenCoordinateDTO dto = new MavenCoordinateDTO();
        dto.setGroupId("com.dubbohelper.test.api");
        dto.setArtifactId("java-dubbohelper-test-api");
        dto.setVersion("1.1.0");
        scanner.loadApplication(dto,docScanPackage);


        String[] docScanPackage1 = {"com.dubbohelper.test2.api.service"};
        MavenCoordinateDTO dto1 = new MavenCoordinateDTO();
        dto1.setGroupId("com.dubbohelper.test2.api");
        dto1.setArtifactId("java-dubbohelper-test2-api");
        dto1.setVersion("1.12.0-SNAPSHOT");
        scanner.loadApplication(dto1,docScanPackage1);

    }
}
