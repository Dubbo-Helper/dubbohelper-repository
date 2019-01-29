package com.dubbohelper.admin.scanner;

import com.dubbohelper.admin.common.enums.FilePathEnum;
import com.dubbohelper.admin.common.util.AnnotationUtil;
import com.dubbohelper.admin.common.util.JarsLoandUtil;
import com.dubbohelper.admin.common.util.MavenUtil;
import com.dubbohelper.admin.dto.MavenCoordDTO;
import com.dubbohelper.admin.dto.MavenDataDTO;
import com.dubbohelper.admin.scanner.elementInfo.ElementInfo;
import com.dubbohelper.admin.service.ConfigureService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ApiDocScanner {
    @Autowired
    private ConfigureService configureService;

    private final static Map<String, Map<ServiceInfo, List<InterfaceInfo>>> JAR_ANNOTATION_CACHE = new ConcurrentHashMap<>();

    public Map<ServiceInfo, List<InterfaceInfo>> getJarAnnotation(MavenCoordDTO dto) {
        String key = dto.getGroupId() + "." + dto.getArtifactId() + "." + dto.getVersion();
        if (!JAR_ANNOTATION_CACHE.containsKey(key)) {
            loadJar(key, dto, new String[]{dto.getGroupId()});
        }

        return JAR_ANNOTATION_CACHE.get(key);
    }

    /**
     * 从缓存中清除
     * @param dto
     */
    public void removeJarAnnotation(MavenCoordDTO dto) {
        String key = dto.getGroupId() + "." + dto.getArtifactId() + "." + dto.getVersion();

        JAR_ANNOTATION_CACHE.remove(key);
    }

    public synchronized void loadJar(String jarName, MavenCoordDTO dto, String... docScanPackages) {
        if (docScanPackages == null || docScanPackages.length == 0) {
            log.info("docScanPackages is null");
            return;
        }
        String jarPath =  FilePathEnum.JARPATH.getAbsolutePath();
        jarPath = jarPath + dto.getGroupId().replace(".", File.separator) + File.separator
                + dto.getArtifactId() + File.separator + dto.getVersion() + File.separator
                + dto.getArtifactId() + "-" + dto.getVersion() + ".jar";
        MavenDataDTO mavenDataDTO = new MavenDataDTO(dto);
        mavenDataDTO.setRepository(configureService.getConfigures().getRepositoryUrl());
        ClassScanner classScanner = null;
        try {
            //加载类
            String classPathAll = MavenUtil.downLoadAll(mavenDataDTO);
            URLClassLoader urlClassLoader = JarsLoandUtil.loanJar(classPathAll.split(":"));
            classScanner = new ClassScanner(urlClassLoader);

            for (String docScanPackage : docScanPackages) {
                Set<Class<?>> apiDocServices = new HashSet<Class<?>>(classScanner.getClasses(jarPath, docScanPackage));
                log.debug("scan @ApidocService size:{}", apiDocServices.size());
                log.debug("scan @ApidocService {}", apiDocServices);
                initService(apiDocServices, jarName);
            }

            //卸载类
            urlClassLoader.close();
        } catch (ArtifactResolutionException e) {
            e.printStackTrace();
        } catch (DependencyCollectionException e) {
            e.printStackTrace();
        } catch (DependencyResolutionException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 解析Service注解
     *
     * @param apiDocServices 接口
     */
    private void initService(Set<Class<?>> apiDocServices, String jarName) {
        Map<ServiceInfo, List<InterfaceInfo>> serviceCache = new HashMap<>();
        if (JAR_ANNOTATION_CACHE.containsKey(jarName)) {
            serviceCache = JAR_ANNOTATION_CACHE.get(jarName);
        }

        for (Class<?> service : apiDocServices) {
            //解析Service
            //ApidocService apidocService = service.getAnnotation(ApidocService.class);
            Annotation apidocServiceAnn = AnnotationUtil.getAnnotation(service.getAnnotations(),"ApidocService");
            String value = "";
            String usage0 = "";
            if (!StringUtils.isEmpty(AnnotationUtil.getAnnotationMemberValue(apidocServiceAnn,"value"))) {
                value = AnnotationUtil.getAnnotationMemberValue(apidocServiceAnn,"value").toString();
            }
            if (!StringUtils.isEmpty(AnnotationUtil.getAnnotationMemberValue(apidocServiceAnn,"usage"))) {
                usage0 = AnnotationUtil.getAnnotationMemberValue(apidocServiceAnn,"usage").toString();
            }
            ServiceInfo serviceInfo = new ServiceInfo(value, service.getName(), usage0);
            List<InterfaceInfo> interfaceInfos = new ArrayList<InterfaceInfo>();
            serviceCache.put(serviceInfo, interfaceInfos);

            Method[] methods = service.getMethods();
            for (Method m : methods) {
                //解析Methods
                //ApidocInterface apidocInterface = m.getAnnotation(ApidocInterface.class);
                Annotation apidocInterfaceAnn = AnnotationUtil.getAnnotation(m.getAnnotations(),"ApidocInterface");
                if (apidocInterfaceAnn == null) {
                    log.info("{} is not use @ApiDocInterface", m);
                    continue;
                }
                String name = service.getName() + "." + m.getName();
                String desc = "";
                String usage = usage0;
                if (!StringUtils.isEmpty(AnnotationUtil.getAnnotationMemberValue(apidocInterfaceAnn,"value"))) {
                    desc = AnnotationUtil.getAnnotationMemberValue(apidocInterfaceAnn,"value").toString();
                }
                if (!StringUtils.isEmpty(AnnotationUtil.getAnnotationMemberValue(apidocInterfaceAnn,"usage"))) {
                    usage = AnnotationUtil.getAnnotationMemberValue(apidocInterfaceAnn,"usage").toString();
                }
                InterfaceInfo interfaceInfo = new InterfaceInfo(name, desc, usage, service.getName(), m.getName(), "", "");
                interfaceInfos.add(interfaceInfo);

                //解析Request、Response
                Class requestClazz = null;
                if (m.getParameterTypes().length > 0) {
                    if (m.getParameterTypes().length == 1) {
                        requestClazz = m.getParameterTypes()[0];
                        interfaceInfo.setRequestName(requestClazz.getName());
                    } else {
                        log.error("{}.{} 存在过多参数,只允许单一参数", service.getName(), m.getName());
                        continue;
                    }
                }
                Class returnClazz = m.getReturnType();
                Set<String> responseSet = new HashSet<String>();
                responseSet.add("ItemResultDTO");
                responseSet.add("ListResultDTO");
                responseSet.add("PaginationResultDTO");
                if (responseSet.contains(returnClazz.getSimpleName())) {
                    Class[] classes = extractInterfaceMethodReturn(m);
                    if (classes.length != 1) {
                        log.error("{}.{} 返回值泛型信息不为一个", service.getName(), m.getName());
                        continue;
                    }
                    returnClazz = classes[0];
                    interfaceInfo.setResponseName(returnClazz.getName());
                }

                ParameterScanner parameterScanner = new ParameterScanner();
                if (requestClazz != null) {
                    List<ElementInfo> requests = parameterScanner.extract(requestClazz);
                    interfaceInfo.getRequest().addAll(requests);
                }
                List<ElementInfo> responses = parameterScanner.extract(returnClazz);
                interfaceInfo.getResponse().addAll(responses);
            }
        }
        JAR_ANNOTATION_CACHE.put(jarName, serviceCache);
    }

    /**
     * 提取接口方法返回上的泛型信息
     *
     * @param method 方法
     * @return 泛型数组
     */
    private static Class[] extractInterfaceMethodReturn(Method method) {
        Type type = method.getGenericReturnType();
        //检查泛型
        if (type instanceof ParameterizedType) {
            //获取泛型
            ParameterizedType target = (ParameterizedType) type;
            Type[] parameters = target.getActualTypeArguments();
            Class[] classes = new Class[parameters.length];
            int idx = 0;
            for (Type type0 : parameters) {
                classes[idx] = (Class) type0;
                idx++;
            }
            return classes;
        }
        return new Class[0];
    }
}
