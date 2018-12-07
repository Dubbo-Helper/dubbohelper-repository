package com.dubbohelper.admin.service;

import com.dubbohelper.admin.dto.MavenCoordDTO;
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
     * 获取服务列表
     *
     * @param dto jar坐标信息
     * @return 服务列表
     */
    List<ServiceInfo> listService(MavenCoordDTO dto);

    /**
     * 获取服务包含方法列表
     *
     * @param dto jar坐标信息
     * @param className   服务名
     * @return
     */
    List<InterfaceInfo> listInterface(MavenCoordDTO dto, String className);

    /**
     * 获取方法详情
     *
     * @param dto jar坐标信息
     * @param className   服务名
     * @param methodName  方法名
     * @return
     */
    InterfaceInfo interfaceDetail(MavenCoordDTO dto, String className, String methodName);

    /**
     * 文件下载
     *
     * @param dto jar坐标信息
     * @param fileName     文件名
     * @param outputStream 文件内容
     */
    void downloadApiDoc(MavenCoordDTO dto, String fileName, OutputStream outputStream);
}
