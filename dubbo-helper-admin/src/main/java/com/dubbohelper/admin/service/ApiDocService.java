package com.dubbohelper.admin.service;

import com.dubbohelper.admin.scanner.InterfaceInfo;
import com.dubbohelper.admin.scanner.ServiceInfo;

import java.io.OutputStream;
import java.util.List;

/**
 * 获取注解信息Service
 *
 * @author lijinbo
 * @since 1.0.0
 */
public interface ApiDocService {

    /**
     * 获取应用列表
     * @return 应用列表
     */
    List<String> listApplication();

    /**
     * 获取服务列表
     * @param packageName 包名
     * @return 服务列表
     */
    List<ServiceInfo> listService(String packageName);

    /**
     * 获取服务包含方法列表
     * @param packageName 包名
     * @param className 服务名
     * @return
     */
    List<InterfaceInfo> listInterface(String packageName, String className);

    /**
     * 获取方法详情
     * @param packageName 包名
     * @param className 服务名
     * @param methodName 方法名
     * @return
     */
    InterfaceInfo interfaceDetail(String packageName, String className, String methodName);

    /**
     * 文件下载
     * @param packageName 包名
     * @param fileName 文件名
     * @param outputStream 文件内容
     */
    void downloadApiDoc(String packageName, String fileName, OutputStream outputStream);
}
