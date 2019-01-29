package com.dubbohelper.admin.common.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengcy on 2019/1/27.
 */
public class JarsLoandUtil {

    /**
     * 通过jarPaths加载文件到classpath。
     * @param
     * @return URLClassLoader
     * @throws Exception 异常
     */
    public static URLClassLoader loanJar(String... jarPaths) {
        if (jarPaths == null) {
            return null;
        }

        URLClassLoader classloader = null;
        try {
            List<URL> urls =new ArrayList<>();

            for (String jarPath : jarPaths) {
                URL url=new URL("file:" + jarPath + "");
                urls.add(url);
            }

            classloader = new URLClassLoader(urls.toArray(new URL[0]), null);
//            for (URL uri : urls) {
//                loandMethod.invoke(classloader, uri);
//            }
        } catch (Exception e) {

        }

        return classloader;
    }
}
