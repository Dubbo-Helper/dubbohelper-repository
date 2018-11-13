package com.dubbohelper.admin.util;

import com.dubbohelper.admin.apidoc.InterfaceInfo;
import com.dubbohelper.admin.apidoc.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.List;
import java.util.Map;

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
            // 设置变量
            VelocityContext ctx = new VelocityContext();
            ctx.put("mapKey", fileName);
            ctx.put("serviceList",INTERFACE_CACHE);
            String str = VelocityUtil.createApiDoc(ctx,"apiDoc.md.vm");

            File file = new File(getFilePath(fileName));
            FileUtils.writeByteArrayToFile(file,str.getBytes());
        } catch (Exception e) {
            log.error("生成文档失败");
        }
    }

    /**
     * 删除临时文件
     */
    public static void deleteApiDocFile(String fileName){
        try {

            FileUtils.forceDeleteOnExit(new File(getFilePath(fileName)));
        } catch (Exception e) {
            log.error("删除文档失败");
        }
    }
}
