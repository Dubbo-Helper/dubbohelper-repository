package com.dubbohelper.admin.common.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * 文件下载工具
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Slf4j
public class FileUtil {

    public static final String CLASS_PATH = FileUtil.class.getResource("/").getPath();

    /**
     * 生成文件
     *
     * @param filePath    路径 + 文件名
     * @param fileContent 文件数据
     */
    public static boolean createFile(String filePath, String fileContent) {
        try {
            File file = new File(filePath);
            FileUtils.writeStringToFile(file, fileContent, "utf-8", false);
        } catch (Exception e) {
            log.error("生成文档失败", e);
            return false;
        }

        return true;
    }

    /**
     * 文件追加内容
     *
     * @param filePath    路径 + 文件名
     * @param fileContent 追加数据
     */
    public static boolean appendContent(String filePath, String fileContent) {
        try {
            File file = new File(filePath);
            FileUtils.writeStringToFile(file, fileContent, "utf-8", true);
        } catch (Exception e) {
            log.error("文件追加内容失败", e);
            return false;
        }

        return true;
    }

    /**
     * 读取文件(一次性读出所有内容)
     *
     * @param filePath 路径 + 文件名
     */
    public static String readFileByString(String filePath) {
        try {
            File file = new File(filePath);
            return FileUtils.readFileToString(file, "utf-8");
        } catch (Exception e) {
            log.error("读取文档失败:{}", filePath, e);
        }
        return null;
    }

    /**
     * 读取文件(按行)
     *
     * @param filePath 路径 + 文件名
     */
    public static List<String> readFileByLine(String filePath) {
        try {
            File file = new File(filePath);
            return FileUtils.readLines(file, "utf-8");
        } catch (Exception e) {
            log.error("读取文档失败:{}", filePath, e);
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param filePath 路径 + 文件名
     */
    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            FileUtils.forceDeleteOnExit(file);
        } catch (Exception e) {
            log.error("删除文档失败:{}", filePath, e);
        }
    }
}
