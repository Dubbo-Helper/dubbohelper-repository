package com.dubbohelper.admin.service.impl;

import com.dubbohelper.admin.common.config.Config;
import com.dubbohelper.admin.dto.MavenDataDTO;
import com.dubbohelper.admin.service.MavenPullService;
import com.dubbohelper.admin.common.util.HttpClient;
import com.dubbohelper.admin.common.util.XMLBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * @author Mr.zhang  2018-11-26 16:33
 */
@Slf4j
@Service
public class MavenPullServiceImpl implements MavenPullService {

    @Autowired
    private Config config;

    @Override
    public void pullApiJar(MavenDataDTO dto) {

        //获取maven-metadata.xml 地址
        StringBuilder repositoryUrl = new StringBuilder();
        repositoryUrl.append(config.getMavenRepositoryUrl());
        repositoryUrl.append(File.separator);
        repositoryUrl.append(dto.getGroupId().replace(".",File.separator)).append(File.separator);
        repositoryUrl.append(dto.getArtifactId()).append(File.separator);
        repositoryUrl.append(dto.getVersion()).append(File.separator);
        repositoryUrl.append("maven-metadata.xml");

        //`
        String xmlStream;
        try {
            xmlStream = HttpClient.downloadXmlFile(repositoryUrl.toString());
        } catch (IOException e) {
            log.error("maven-metadata.xml文件下载失败:{}", dto.toString(), e);
            return;
        }

        //解析 maven-metadata.xml
        MavenDataDTO mavenDataDTO;
        try {
            mavenDataDTO = (MavenDataDTO) XMLBase.xml2obj(MavenDataDTO.class, xmlStream);
        } catch (Exception e) {
            log.error("maven-metadata.xml文件解析失败:{}", xmlStream, e);
            return;
        }

        //下载jar包 保存文件
        if (mavenDataDTO != null) {
            try {
                StringBuilder fileName = new StringBuilder();
                fileName.append(mavenDataDTO.getArtifactId());
                fileName.append("-");
                fileName.append(mavenDataDTO.getValue());
                fileName.append(".");
                fileName.append(mavenDataDTO.getExtension());
                //下载地址
                StringBuilder pullUrl = new StringBuilder();
                pullUrl.setLength(0);
                pullUrl.append(config.getMavenRepositoryUrl());
                pullUrl.append(File.separator);
                pullUrl.append(mavenDataDTO.getGroupId().replace(".",File.separator)).append(File.separator);
                pullUrl.append(mavenDataDTO.getArtifactId()).append(File.separator);
                pullUrl.append(mavenDataDTO.getVersion()).append(File.separator);

                pullUrl.append(fileName);

                String saveJARPath = saveJARPath(mavenDataDTO);
                createFile(saveJARPath);

                HttpClient.downLoadJARUrl(pullUrl.toString(),fileName.toString(),saveJARPath);
            } catch (Exception e) {
                log.error("jar包下载失败:{}", dto.toString(), e);
            }
        }
    }


    /***
     * 拼装jar包下载
     * @param dto
     * @return
     */
    public static String saveJARPath(MavenDataDTO dto){
        StringBuffer sb = new StringBuffer();
        sb.append(System.getProperties().getProperty("user.home")).append(File.separator);
        sb.append(".dubbohelper").append(File.separator);
        sb.append("jars").append(File.separator);
        sb.append(dto.getGroupId().replace(".", File.separator));
        sb.append(dto.getArtifactId()).append(File.separator);
        sb.append(dto.getVersion()).append(File.separator);
        return sb.toString();

    }


    /**
     * 新建文件.
     *
     * @param path 文件路径
     * @throws Exception
     */
    public static void createFile(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        try {
            // 获得文件对象
            File f = new File(path);
            if (f.exists()) {
                return;
            }
            if (!f.exists()) {
                f.mkdirs();// 目录不存在的情况下，创建目录。
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
