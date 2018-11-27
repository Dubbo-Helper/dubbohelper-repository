package com.dubbohelper.admin.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author Mr.zhang  2018-11-26 16:33
 */
public class HttpClient {

//    public static String doGet(String url) {
//        CloseableHttpClient httpClient = null;
//        CloseableHttpResponse response = null;
//        String result = "";
//        try {
//            // 通过址默认配置创建一个httpClient实例
//            httpClient = HttpClients.createDefault();
//            // 创建httpGet远程连接实例
//            HttpGet httpGet = new HttpGet(url);
//            // 设置请求头信息，鉴权
////            httpGet.setHeader("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
//            // 设置配置请求参数
//            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
//                    .setConnectionRequestTimeout(35000)// 请求超时时间
//                    .setSocketTimeout(60000)// 数据读取超时时间
//                    .build();
//            // 为httpGet实例设置配置
//            httpGet.setConfig(requestConfig);
//            // 执行get请求得到返回对象
//            response = httpClient.execute(httpGet);
//            // 通过返回对象获取返回数据
//            HttpEntity entity = response.getEntity();
//            // 通过EntityUtils中的toString方法将结果转换为字符串
//            result = EntityUtils.toString(entity);
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            // 关闭资源
//            if (null != response) {
//                try {
//                    response.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (null != httpClient) {
//                try {
//                    httpClient.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return result;
//    }
//
//    public static String doPost(String url, Map<String, Object> paramMap) {
//        CloseableHttpClient httpClient = null;
//        CloseableHttpResponse httpResponse = null;
//        String result = "";
//        // 创建httpClient实例
//        httpClient = HttpClients.createDefault();
//        // 创建httpPost远程连接实例
//        HttpPost httpPost = new HttpPost(url);
//        // 配置请求参数实例
//        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
//                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
//                .setSocketTimeout(60000)// 设置读取数据连接超时时间
//                .build();
//        // 为httpPost实例设置配置
//        httpPost.setConfig(requestConfig);
//        // 设置请求头
//        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        // 封装post请求参数
//        if (null != paramMap && paramMap.size() > 0) {
//            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//            // 通过map集成entrySet方法获取entity
//            Set<Entry<String, Object>> entrySet = paramMap.entrySet();
//            // 循环遍历，获取迭代器
//            Iterator<Entry<String, Object>> iterator = entrySet.iterator();
//            while (iterator.hasNext()) {
//                Entry<String, Object> mapEntry = iterator.next();
//                nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
//            }
//
//            // 为httpPost设置封装好的请求参数
//            try {
//                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            // httpClient对象执行post请求,并返回响应参数对象
//            httpResponse = httpClient.execute(httpPost);
//            // 从响应对象中获取响应内容
//            HttpEntity entity = httpResponse.getEntity();
//            result = EntityUtils.toString(entity);
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            // 关闭资源
//            if (null != httpResponse) {
//                try {
//                    httpResponse.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (null != httpClient) {
//                try {
//                    httpClient.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return result;
//    }



    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @throws IOException
     */
    public static String  downLoadFromUrl(String urlStr) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为30秒
        conn.setConnectTimeout(3*10000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//        conn.setRequestProperty("lfwywxqyh_token",toekn);

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);
        String s = new String(getData);
        return s;
//        //文件保存位置
//        File saveDir = new File(savePath);
//        if(!saveDir.exists()){
//            saveDir.mkdir();
//        }
//        File file = new File(saveDir+File.separator+fileName);
//        FileOutputStream fos = new FileOutputStream(file);
//        fos.write(getData);
//        if(fos!=null){
//            fos.close();
//        }
//        if(inputStream!=null){
//            inputStream.close();
//        }


//        System.out.println("info:"+url+" download success");

    }

    /**
     * 从网络Url中下载jar文件
     * @param urlStr
     * @throws IOException
     */
    public static void   downLoadJARUrl(String urlStr,String fileName,String savePath) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//        conn.setRequestProperty("lfwywxqyh_token",toekn);

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);
        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.getParentFile().exists()){
            saveDir.getParentFile().mkdir();
        }
        File file = new File(saveDir+File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }


        System.out.println("info:"+url+" download success");

    }
    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void main(String[] args) {
        System.out.println(System.getProperties().getProperty("user.name"));
        System.out.println(System.getProperties().getProperty("user.home"));
        System.out.println(System.getProperties().getProperty("user.dir"));
    }



}