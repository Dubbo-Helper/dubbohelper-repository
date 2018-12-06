package com.dubbohelper.admin.service.impl;

import com.dubbohelper.admin.common.enums.FileTypeEnum;
import com.dubbohelper.admin.scanner.ApiDocScanner;
import com.dubbohelper.admin.scanner.InterfaceInfo;
import com.dubbohelper.admin.scanner.ServiceInfo;
import com.dubbohelper.admin.service.ApiDocService;
import com.dubbohelper.admin.common.util.FileUtil;
import lombok.SneakyThrows;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lijinbo
 * @since 1.0.0
 */
@Service
public class ApiDocServiceImpl implements ApiDocService {

    @Override
    public List<String> listApplication() {
        List<String> list = new ArrayList<String>(ApiDocScanner.getAPPLICATION_CACHE().keySet());
        Collections.sort(list);
        return list;
    }

    @Override
    public List<ServiceInfo> listService(String packageName) {
        List<ServiceInfo> list = new ArrayList<ServiceInfo>(ApiDocScanner.getAPPLICATION_CACHE().get(packageName).keySet());
        Collections.sort(list);
        return list;
    }

    @Override
    public List<InterfaceInfo> listInterface(String packageName, String className) {
        for (ServiceInfo serviceInfo : ApiDocScanner.getAPPLICATION_CACHE().get(packageName).keySet()) {
            if (serviceInfo.getClassName().equals(className)) {
                List<InterfaceInfo> list = ApiDocScanner.getAPPLICATION_CACHE().get(packageName).get(serviceInfo);
                Collections.sort(list);
                return list;
            }
        }
        return null;
    }

    @Override
    public InterfaceInfo interfaceDetail(String packageName, String className, String methodName) {
        for (ServiceInfo serviceInfo : ApiDocScanner.getAPPLICATION_CACHE().get(packageName).keySet()) {
            if (serviceInfo.getClassName().equals(className)) {
                List<InterfaceInfo> interfaceInfoList = ApiDocScanner.getAPPLICATION_CACHE().get(packageName).get(serviceInfo);
                for (InterfaceInfo interfaceInfo : interfaceInfoList) {
                    if (interfaceInfo.getMethodName().equals(methodName)) {
                        return interfaceInfo;
                    }
                }
            }
        }
        return null;
    }

    @SneakyThrows
    @Override
    public void downloadApiDoc(String packageName, String fileName, OutputStream outputStream) {
        // 设置Velocity变量
        VelocityContext ctx = new VelocityContext();
        ctx.put("mapKey", fileName);
        ctx.put("serviceList",ApiDocScanner.getAPPLICATION_CACHE().get(packageName));
        // 初始化Velocity模板引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        ve.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        ve.init();
        // 获取Velocity模板文件
        Template template = ve.getTemplate("apiDoc.md.vm");
        // 输出
        StringWriter sw = new StringWriter();
        template.merge(ctx,sw);
        FileUtil.createFile(FileTypeEnum.API_DOC, fileName, sw.toString());

        File file = new File(FileUtil.getFilePath(FileTypeEnum.API_DOC, fileName));
        if(!file.exists()){
            throw new FileNotFoundException("文件不存在");
        }
        FileInputStream in = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = in.read(buffer)) > 0){
            outputStream.write(buffer,0,length);
        }

        FileUtil.deleteFile(FileTypeEnum.API_DOC, fileName);
    }
}
