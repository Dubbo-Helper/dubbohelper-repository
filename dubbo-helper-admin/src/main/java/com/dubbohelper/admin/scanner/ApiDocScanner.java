package com.dubbohelper.admin.scanner;

import com.dubbohelper.admin.common.enums.FilePathEnum;
import com.dubbohelper.admin.dto.MavenCoordDTO;
import com.dubbohelper.admin.scanner.elementInfo.ElementInfo;
import com.dubbohelper.common.annotations.ApidocInterface;
import com.dubbohelper.common.annotations.ApidocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    private final static Map<String, Map<ServiceInfo, List<InterfaceInfo>>> JAR_ANNOTATION_CACHE = new ConcurrentHashMap<>();

    public Map<ServiceInfo, List<InterfaceInfo>> getJarAnnotation(MavenCoordDTO dto) {
        String key = dto.getGroupId() + "." + dto.getArtifactId() + "." + dto.getVersion();
        if (!JAR_ANNOTATION_CACHE.containsKey(key)) {
            String jarPath =  FilePathEnum.JARPATH.getRelativePath();
            jarPath = jarPath + dto.getGroupId().replace(".", File.separator) + File.separator
                    + dto.getArtifactId() + File.separator + dto.getVersion() + File.separator
                    + dto.getArtifactId() + "-" + dto.getVersion() + ".jar";
            loadJar(key, jarPath, new String[]{dto.getGroupId()});
        }

        return JAR_ANNOTATION_CACHE.get(key);
    }

    public synchronized void loadJar(String jarName, String jarPath, String... docScanPackages) {
        if (docScanPackages == null || docScanPackages.length == 0) {
            log.info("docScanPackages is null");
            return;
        }

        ClassScanner classScanner = new ClassScanner();
        for (String docScanPackage : docScanPackages) {
            Set<Class<?>> apiDocServices = new HashSet<Class<?>>(classScanner.getClasses(jarPath, docScanPackage));
            log.debug("scan @ApidocService size:{}", apiDocServices.size());
            log.debug("scan @ApidocService {}", apiDocServices);
            initService(apiDocServices, jarName);
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
            ApidocService apidocService = service.getAnnotation(ApidocService.class);
            String value = "";
            String usage0 = "";
            if (!StringUtils.isEmpty(apidocService.value())) {
                value = apidocService.value();
            }
            if (!StringUtils.isEmpty(apidocService.usage())) {
                usage0 = apidocService.usage();
            }
            ServiceInfo serviceInfo = new ServiceInfo(value, service.getName(), usage0);
            List<InterfaceInfo> interfaceInfos = new ArrayList<InterfaceInfo>();
            serviceCache.put(serviceInfo, interfaceInfos);

            Method[] methods = service.getMethods();
            for (Method m : methods) {
                //解析Methods
                ApidocInterface apidocInterface = m.getAnnotation(ApidocInterface.class);
                if (apidocInterface == null) {
                    log.info("{} is not use @ApiDocInterface", m);
                    continue;
                }
                String name = service.getName() + "." + m.getName();
                String desc = "";
                String usage = usage0;
                if (!StringUtils.isEmpty(apidocInterface.value())) {
                    desc = apidocInterface.value();
                }
                if (!StringUtils.isEmpty(apidocInterface.usage())) {
                    usage = apidocInterface.usage();
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

    public static void main(String[] args) {

        ApiDocScanner scanner = new ApiDocScanner();
        MavenCoordDTO dto = new MavenCoordDTO();
        dto.setGroupId("com.dubbohelper.test.api");
        dto.setArtifactId("java-dubbohelper-test-api");
        dto.setVersion("1.1.0");
        scanner.getJarAnnotation(dto);

        MavenCoordDTO dto1 = new MavenCoordDTO();
        dto1.setGroupId("com.dubbohelper.test2.api");
        dto1.setArtifactId("java-dubbohelper-test2-api");
        dto1.setVersion("1.12.0-SNAPSHOT");
        scanner.getJarAnnotation(dto1);

        MavenCoordDTO dto2 = new MavenCoordDTO();
        dto2.setGroupId("com.dubbohelper.test.api");
        dto2.setArtifactId("java-dubbohelper-test-api");
        dto2.setVersion("1.1.0");
        scanner.getJarAnnotation(dto2);
    }
}
