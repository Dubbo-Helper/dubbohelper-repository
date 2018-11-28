package com.dubbohelper.admin.service.impl;

import com.dubbohelper.admin.dto.MavenCoordinateDTO;
import com.dubbohelper.admin.scanner.ApiDocScanner;
import com.dubbohelper.admin.scanner.InterfaceInfo;
import com.dubbohelper.admin.scanner.ServiceInfo;
import com.dubbohelper.admin.service.ApiDocService;
import com.dubbohelper.admin.service.MavenPullService;
import com.dubbohelper.admin.util.FileUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lijinbo
 * @since 1.0.0
 */
@Service
public class ApiDocServiceImpl implements ApiDocService {

    @Autowired
    private MavenPullService mavenPullService;

    @Override
    public void loadApplication(MavenCoordinateDTO dto) {
        mavenPullService.pullApiJar(dto);

    }

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
        FileUtil.createApiDocFile(ApiDocScanner.getAPPLICATION_CACHE().get(packageName),fileName);

        File file = new File(FileUtil.getFilePath(fileName));
        if(!file.exists()){
            throw new FileNotFoundException("文件不存在");
        }
        FileInputStream in = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = in.read(buffer)) > 0){
            outputStream.write(buffer,0,length);
        }

        FileUtil.deleteApiDocFile(fileName);
    }
}
