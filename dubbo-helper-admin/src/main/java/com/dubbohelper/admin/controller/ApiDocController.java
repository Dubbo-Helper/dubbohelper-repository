package com.dubbohelper.admin.controller;

import com.dubbohelper.admin.scanner.ApiDocScanner;
import com.dubbohelper.admin.scanner.InterfaceInfo;
import com.dubbohelper.admin.scanner.ServiceInfo;
import com.dubbohelper.admin.util.ModelUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/apiDoc")
public class ApiDocController implements InitializingBean {

    @Setter
    private String[] docScanPackage = {"com.zbj.finance.cashloan.api.service"};

    private ApiDocScanner scanner = new ApiDocScanner();

    @RequestMapping("")
    public ModelAndView listServices() throws Exception{
        List<ServiceInfo> serviceList  = scanner.listService();

        ModelAndView modelAndView = new ModelAndView("index");
        InterfaceInfo currentInterfaceInfo = new InterfaceInfo();
        ModelUtil.getModel(modelAndView,serviceList,null,currentInterfaceInfo,null,null);

        return modelAndView;
    }

    @RequestMapping("/method")
    public ModelAndView listInterfaces(String service) throws Exception{
        List<ServiceInfo> serviceList  = scanner.listService();
        List<InterfaceInfo> interfaceList = scanner.listInterface(service);

        ModelAndView modelAndView = new ModelAndView("index");
        InterfaceInfo currentInterfaceInfo = new InterfaceInfo();
        currentInterfaceInfo.setClassName(service);
        ModelUtil.getModel(modelAndView,serviceList,interfaceList,currentInterfaceInfo,null,null);

        return modelAndView;
    }

    @RequestMapping("/document")
    public ModelAndView listInterface(String service, String method) throws Exception{

        List<ServiceInfo> serviceList  = scanner.listService();
        List<InterfaceInfo> interfaceList = scanner.listInterface(service);
        InterfaceInfo interfaceInfo = scanner.interfaceDetail(service, method);

        ModelAndView modelAndView = new ModelAndView("index");
        ModelUtil.getModel(modelAndView,serviceList,interfaceList,interfaceInfo, interfaceInfo.getRequest(),interfaceInfo.getResponse());

        return modelAndView;
    }

    @RequestMapping("downloadApiDoc")
    public void downloadApiDoc(HttpServletRequest request, HttpServletResponse response) {
        OutputStream outputStream = null;
        String projectName = "test";
        try{
            outputStream = response.getOutputStream();
            response.reset();
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName="+ projectName +".md");
            scanner.downloadApiDoc(projectName,outputStream);
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

    @Override
    public void afterPropertiesSet() throws Exception {
        scanner.init(docScanPackage);
    }
}
