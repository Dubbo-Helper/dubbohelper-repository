package com.dubbohelper.admin.service.impl;

import com.dubbohelper.admin.config.Config;
import com.dubbohelper.admin.dto.MavenDataDTO;
import com.dubbohelper.admin.service.MavenPullService;
import com.dubbohelper.admin.util.HttpClient;
import com.dubbohelper.admin.util.XMLBase;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

/**
 * @Author Mr.zhang  2018-11-26 16:33
 */
public class MavenPullServiceImpl implements MavenPullService {
    @Autowired
    private Config config;
    @Override
    public void pull(String groupId, String artifactId, String version) {
        //获取仓库maven-metadata.xml 地址
        String repositoryUrl = getMavenUrl(groupId, artifactId, version);

        String s = "";
        try {
            s = HttpClient.downLoadFromUrl(repositoryUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //解析 maven-metadata.xml
        MavenDataDTO data = null;
        try {
            data = (MavenDataDTO) XMLBase.xml2obj(MavenDataDTO.class, s);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //下载jar包 保存文件
        if (data != null) {
            try {
                StringBuffer fileName = new StringBuffer();
                fileName.append(data.getArtifactId());
                fileName.append("-");
                fileName.append(data.getValue());
                fileName.append(".");
                fileName.append(data.getExtension());
                //下载地址
                StringBuffer pullUrl = new StringBuffer();
                pullUrl.setLength(0);
                pullUrl.append(config.getMavenRepositoryUrl());
                pullUrl.append(File.separator);
                pullUrl.append(data.getGroupId().replace(".",File.separator)).append(File.separator);
                pullUrl.append(data.getArtifactId()).append(File.separator);
                pullUrl.append(data.getVersion()).append(File.separator);

                pullUrl.append(fileName);

                String saveJARPath = saveJARPath(data);
                createFile(saveJARPath);

                HttpClient.downLoadJARUrl(pullUrl.toString(),fileName.toString(),saveJARPath);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 获取Maven仓库地址
     * @param groupId
     * @param artifactId
     * @param version
     * @return
     */
    private String getMavenUrl(String groupId, String artifactId, String version) {
        StringBuffer sb = new StringBuffer();
        sb.append(config.getMavenRepositoryUrl());
        sb.append(File.separator);
        sb.append(groupId.replace(".",File.separator)).append(File.separator);
        sb.append(artifactId).append(File.separator);
        sb.append(version).append(File.separator);
        sb.append("maven-metadata.xml");
        return sb.toString();
    }


    /***
     * 拼装jar包下载
     * @param data
     * @return
     */
    public static String saveJARPath(MavenDataDTO data){
        StringBuffer sb = new StringBuffer();
        sb.append(System.getProperties().getProperty("user.home")).append(File.separator);
        sb.append(".dubbohelper").append(File.separator);
        sb.append("jars").append(File.separator);
        sb.append(data.getGroupId().replace(".", File.separator));
        sb.append(data.getArtifactId()).append(File.separator);
        sb.append(data.getVersion()).append(File.separator);
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
