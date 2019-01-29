package com.dubbohelper.admin.scanner;

import com.dubbohelper.admin.common.util.AnnotationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 动态加载Class
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
public class ClassScanner {
    private  URLClassLoader urlClassLoader;

    /**
     * 从包package中获取Class
     *
     * @param jarPath jar包文件路径
     * @param packageName 需扫描的包名
     */
    public Set<Class<?>> getClasses(String jarPath, String packageName) {
        // class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        String packageDirName = packageName.replace('.', '/');
        try {
            URL classLoaderUrl = new URL("file:" + jarPath);
            URL url = new URL("jar:file:" + jarPath + "!/");
            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                // 如果是以/开头的
                if (name.charAt(0) == '/') {
                    // 获取后面的字符串
                    name = name.substring(1);
                }

                int idx = name.lastIndexOf('/');
                // 如果以"/"结尾 是一个包
                if (idx != -1) {
                    // 获取包名 把"/"替换成"."
                    packageName = name.substring(0, idx).replace('/', '.');
                }
                // 如果可以迭代下去 并且是一个包
                if (idx != -1) {
                    if (name.endsWith(".class") && !entry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        try {
                             //urlClassLoader = new URLClassLoader(new URL[] { classLoaderUrl }, Thread.currentThread().getContextClassLoader());
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                Class<?> cls = urlClassLoader.loadClass(packageName + '.' + className);
                                if (cls.isInterface()) {
                                    Annotation apidocService = AnnotationUtil.getAnnotation(cls.getAnnotations(),"ApidocService");
                                    if (apidocService != null) {
                                        classes.add(cls);
                                    } else {
                                        log.info("{} is not use @ApiDocService", cls);
                                    }
                                } else {
                                    log.info("{} is not interface", cls);
                                }
                            } else {
                                urlClassLoader.loadClass(packageName + '.' + className);
                            }
                        } catch (ClassNotFoundException e) {
                            log.error("找不到此类{}的.class文件",packageName + '.' + className, e);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("解析jar包失败{}", jarPath, e);
        }

        return classes;
    }
}
