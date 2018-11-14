package com.dubbohelper.admin.util;

import com.dubbohelper.admin.scanner.InterfaceInfo;
import com.dubbohelper.admin.scanner.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * 文件下载工具
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Slf4j
public class FileUtil {

    private static final String CLASS_PATH = FileUtil.class.getResource("/").getPath();

    private static final String DOC_BASE_PATH = CLASS_PATH + "/apidoc/";

    public static String getFilePath(String fileName) {

        return DOC_BASE_PATH + fileName + ".md";
    }

    /**
     * 生成临时文件
     * @param INTERFACE_CACHE 文件数据
     */
    public static void createApiDocFile(Map<ServiceInfo, List<InterfaceInfo>> INTERFACE_CACHE, String fileName){
        try {
            // 设置Velocity变量
            VelocityContext ctx = new VelocityContext();
            ctx.put("mapKey", fileName);
            ctx.put("serviceList",INTERFACE_CACHE);
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
            String fileContent = sw.toString();

            File file = new File(getFilePath(fileName));
            FileUtils.writeByteArrayToFile(file,fileContent.getBytes());
        } catch (Exception e) {
            log.error("生成文档失败", e);
        }
    }

    /**
     * 删除临时文件
     */
    public static void deleteApiDocFile(String fileName){
        try {

            FileUtils.forceDeleteOnExit(new File(getFilePath(fileName)));
        } catch (Exception e) {
            log.error("删除文档失败:{}", fileName, e);
        }
    }
}
