package com.dubbohelper.admin.scanner;

import com.dubbohelper.admin.dto.MavenCoordinateDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
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
public class ClassScanner {

	/**
     * 从包package中获取所有的Class
     *
     * @param dto maven坐标
     * @param packageName 包名
     * @param repositoryPath jar包仓库路径
     */
    public Set<Class<?>> getClasses(MavenCoordinateDTO dto, String packageName, String repositoryPath) {
        // class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        String packageDirName = packageName.replace('.', '/');
        StringBuilder jarPath = new StringBuilder(repositoryPath);
        jarPath.append(dto.getGroupId().replace(".","/")).append("/");
        jarPath.append(dto.getArtifactId()).append("/");
        jarPath.append(dto.getVersion()).append("/");
        jarPath.append(dto.getArtifactId()).append("-").append(dto.getVersion()).append(".jar");
        try {
            URL classLoaderUrl = new URL("file:" + jarPath);
            URL url = new URL("jar:file:" + jarPath.append("!/"));
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
                // 如果前半部分和定义的包名相同

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
                                URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { classLoaderUrl }, Thread.currentThread().getContextClassLoader());
                                if (name.startsWith(packageDirName)) {
                                    classes.add(urlClassLoader.loadClass(packageName + '.' + className));
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
