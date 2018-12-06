package com.dubbohelper.admin.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Mr.zhang  2018-11-26 16:33
 */
@Slf4j
public class HttpClient {

    /**
     * 下载 maven-metadata.xml
     * @param xmlUrl 下载地址
     * @throws IOException
     */
    public static String downloadXmlFile(String xmlUrl) throws IOException{
        URL url = new URL(xmlUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为30秒
        conn.setConnectTimeout(3*10000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //conn.setRequestProperty("lfwywxqyh_token",toekn);

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] xmlStream = readInputStream(inputStream);
        return new String(xmlStream);

    }

    /**
     * 下载 jar包
     * @param jarUrl 下载地址
     * @throws IOException
     */
    public static void downLoadJARUrl(String jarUrl,String fileName,String savePath) throws IOException{
        URL url = new URL(jarUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //conn.setRequestProperty("lfwywxqyh_token",toekn);

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] jarStream = readInputStream(inputStream);
        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.getParentFile().exists()){
            saveDir.getParentFile().mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileUtils.writeByteArrayToFile(file,jarStream);
        log.info("jar包下载成功:{}",fileName);
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}