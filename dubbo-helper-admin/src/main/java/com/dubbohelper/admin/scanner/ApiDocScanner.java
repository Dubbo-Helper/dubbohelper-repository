package com.dubbohelper.admin.scanner;

import com.dubbohelper.admin.dto.MavenCoordinateDTO;
import com.dubbohelper.admin.scanner.elementInfo.ElementInfo;
import com.dubbohelper.common.annotations.ApidocInterface;
import com.dubbohelper.common.annotations.ApidocService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

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
public class ApiDocScanner {

    @Getter
    private final static Map<String,Map<ServiceInfo, List<InterfaceInfo>>> APPLICATION_CACHE = new ConcurrentHashMap<>();

    public synchronized void loadApplication(MavenCoordinateDTO dto, String...docScanPackages) {
        if(docScanPackages == null || docScanPackages.length == 0){
            log.info("docScanPackages is null");
            return;
        }

        ClassScanner classScanner = new ClassScanner();
        for (String docScanPackage : docScanPackages){
            Set<Class<?>> apiDocServices = new HashSet<Class<?>>();
            apiDocServices.addAll(classScanner.getClasses(dto,docScanPackage,"/Users/lijinbo/.m2/repository/"));
            log.debug("scan @ApidocService size:{}", apiDocServices.size());
            log.debug("scan @ApidocService {}", apiDocServices);
            initService(apiDocServices,docScanPackage);
        }
    }

    /**
     * 解析Service注解
     * @param apiDocServices 接口
     */
    private void initService(Set<Class<?>> apiDocServices,String packageName) {
        Map<ServiceInfo, List<InterfaceInfo>> serviceCache = new HashMap<>();
        for (Class<?> service : apiDocServices) {
            if (!service.isInterface()) {
                log.error("{} is not interface", service);
                continue;
            }

            //解析Service
            ApidocService apidocService = service.getAnnotation(ApidocService.class);
            String usage0 = "";
            String value = "";
            if (apidocService != null) {
                if (!StringUtils.isEmpty(apidocService.usage())) {
                    usage0 = apidocService.usage();
                }
                if (!StringUtils.isEmpty(apidocService.value())) {
                    value = apidocService.value();
                }
            }
            ServiceInfo serviceInfo = new ServiceInfo(value, service.getName(), usage0);
            List<InterfaceInfo> interfaceInfos = new ArrayList<InterfaceInfo>();
            serviceCache.put(serviceInfo, interfaceInfos);

            Method[] methods = service.getMethods();
            for (Method m : methods) {
                //解析Methods
                ApidocInterface apidocInterface = m.getAnnotation(ApidocInterface.class);
                String name = service.getName() + "." + m.getName();
                String usage = usage0;
                String desc = "";
                if (apidocInterface != null) {
                    desc = apidocInterface.value();
                    if (!StringUtils.isEmpty(apidocInterface.usage())) {
                        usage = apidocInterface.usage();
                    }
                }
                InterfaceInfo interfaceInfo = new InterfaceInfo(name,desc, usage, service.getName(), m.getName(),"","");
                interfaceInfos.add(interfaceInfo);

                //解析Request、Response
                Class requestClazz = null;
                if (m.getParameterTypes().length > 0) {
                    if (m.getParameterTypes().length == 1) {
                        requestClazz = m.getParameterTypes()[0];
                        interfaceInfo.setRequestName(requestClazz.getName());
                    } else {
                        log.error("{}.{} 存在过多参数,只允许单一参数", service.getName(), m.getName());
                        break;
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
                        break;
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
        APPLICATION_CACHE.put(packageName,serviceCache);
    }

    /**
     * 提取接口方法返回上的泛型信息
     *
     * @param method 方法
     * @return 泛型数组
     */
    private static Class[] extractInterfaceMethodReturn(Method method){
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
