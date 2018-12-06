package com.dubbohelper.admin.common.util;

import com.dubbohelper.admin.common.enums.FileTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * 文件下载工具
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Slf4j
public class FileUtil {

    private static final String CLASS_PATH = FileUtil.class.getResource("/").getPath();


    public static String getFilePath(FileTypeEnum fileType, String fileName) {

        return CLASS_PATH + fileType.getPath() +fileName + fileType.getSuffix();
    }

    /**
     * 生成文件
     *
     * @param fileName    文件名
     * @param fileContent 文件数据
     */
    public static void createFile(FileTypeEnum fileType, String fileName, String fileContent) {
        try {
            File file = new File(getFilePath(fileType, fileName));
            FileUtils.writeByteArrayToFile(file, fileContent.getBytes());
        } catch (Exception e) {
            log.error("生成文档失败", e);
        }
    }

    /**
     * 读取文件
     *
     * @param fileName 文件名
     */
    public static String readFile(FileTypeEnum fileType, String fileName) {
        try {
            File file = new File(getFilePath(fileType, fileName));
            return FileUtils.readFileToString(file, "utf-8");
        } catch (Exception e) {
            log.error("读取文档失败:{}", fileName, e);
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     */
    public static void deleteFile(FileTypeEnum fileType, String fileName) {
        try {
            File file = new File(getFilePath(fileType, fileName));
            FileUtils.forceDeleteOnExit(file);
        } catch (Exception e) {
            log.error("删除文档失败:{}", fileName, e);
        }
    }
}
