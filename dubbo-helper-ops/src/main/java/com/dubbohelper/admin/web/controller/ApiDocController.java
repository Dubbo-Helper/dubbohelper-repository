package com.dubbohelper.admin.web.controller;

import com.dubbohelper.admin.apidoc.ApiDocScanner;
import com.dubbohelper.admin.apidoc.InterfaceInfo;
import com.dubbohelper.admin.apidoc.ServiceInfo;
import com.dubbohelper.admin.template.ApiTemplateExtractor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/apiDoc")
public class ApiDocController implements InitializingBean {

    @Setter
    private String[] docScanPackage = {"com.zbj.finance.cashloan.api.service"};

    private ApiDocScanner scanner = new ApiDocScanner();

    @RequestMapping("")
    public String listServices(Model model) throws Exception{
        List<ServiceInfo> serviceList  = scanner.listService();
        ApiTemplateExtractor apiTemplateExtractor = new ApiTemplateExtractor();

        InterfaceInfo currentInterfaceInfo = new InterfaceInfo();
        apiTemplateExtractor.buildDocBody(model,serviceList,null,currentInterfaceInfo,null,null);

        return "index";
    }

    @RequestMapping("/method")
    public String listInterfaces(Model model, String service) throws Exception{
        List<ServiceInfo> serviceList  = scanner.listService();
        List<InterfaceInfo> interfaceList = scanner.listInterface(service);
        ApiTemplateExtractor apiTemplateExtractor = new ApiTemplateExtractor();

        InterfaceInfo currentInterfaceInfo = new InterfaceInfo();
        currentInterfaceInfo.setClassName(service);
        apiTemplateExtractor.buildDocBody(model,serviceList,interfaceList,currentInterfaceInfo,null,null);

        return "index";
    }

    @RequestMapping("/document")
    public String listInterface(Model model,String service, String method) throws Exception{

        List<ServiceInfo> serviceList  = scanner.listService();
        List<InterfaceInfo> interfaceList = scanner.listInterface(service);
        InterfaceInfo interfaceInfo = scanner.interfaceDetail(service, method);
        ApiTemplateExtractor apiTemplateExtractor = new ApiTemplateExtractor();

        apiTemplateExtractor.buildDocBody(model,serviceList,interfaceList,interfaceInfo, interfaceInfo.getRequest(),interfaceInfo.getResponse());

        return "index";
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
        scanner.init(false,docScanPackage);
    }
}
