package com.dubbohelper.admin.scanner;

import com.dubbohelper.admin.scanner.elementInfo.ElementInfo;
import com.dubbohelper.admin.util.FileUtil;
import com.dubbohelper.common.annotations.ApidocInterface;
import com.dubbohelper.common.annotations.ApidocService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ApiDocScanner {
    private final Map<ServiceInfo, List<InterfaceInfo>> INTERFACE_CACHE = new ConcurrentHashMap<ServiceInfo, List<InterfaceInfo>>();

    @SneakyThrows
    public void downloadApiDoc(String fileName, OutputStream outputStream) {
        FileUtil.createApiDocFile(INTERFACE_CACHE,fileName);

        File file = new File(FileUtil.getFilePath(fileName));
        if(!file.exists()){
            throw new FileNotFoundException("文件不存在");
        }
        FileInputStream in = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = in.read(buffer)) > 0){
            outputStream.write(buffer,0,length);
        }

        FileUtil.deleteApiDocFile(fileName);
    }

    /**
     * 服务列表
     */
    public List<ServiceInfo> listService() {
        List<ServiceInfo> list = new ArrayList<ServiceInfo>(INTERFACE_CACHE.keySet());
        Collections.sort(list);
        return list;
    }

    /**
     * 服务包含方法列表
     * @param className 服务名
     */
    public List<InterfaceInfo> listInterface(String className) {
        for (ServiceInfo serviceInfo : INTERFACE_CACHE.keySet()) {
            if (serviceInfo.getClassName().equals(className)) {
                List<InterfaceInfo> list = INTERFACE_CACHE.get(serviceInfo);
                Collections.sort(list);
                return list;
            }
        }
        return null;
    }

    /**
     * 方法详情
     * @param className 服务名
     * @param methodName 方法名
     */
    public InterfaceInfo interfaceDetail(String className, String methodName) {
        for (ServiceInfo serviceInfo : INTERFACE_CACHE.keySet()) {
            if (serviceInfo.getClassName().equals(className)) {
                List<InterfaceInfo> interfaceInfoList = INTERFACE_CACHE.get(serviceInfo);
                for (InterfaceInfo interfaceInfo : interfaceInfoList) {
                    if (interfaceInfo.getMethodName().equals(methodName)) {
                        return interfaceInfo;
                    }
                }
            }
        }
        return null;
    }

    public synchronized void init(String...docScanPackages) {
        if(docScanPackages == null || docScanPackages.length == 0){
            log.info("docScanPackages is null");
            return;
        }

        Set<Class<?>> apiDocServices = new HashSet<Class<?>>();
        ClassScanner classScanner = new ClassScanner();
        for (String docScanPackage : docScanPackages){
            apiDocServices.addAll(classScanner.getClasses(docScanPackage));
        }
        log.debug("scan @ApidocService size:{}", apiDocServices.size());
        log.debug("scan @ApidocService {}", apiDocServices);

        initService(apiDocServices);
    }

    /**
     * 解析Service注解
     * @param apiDocServices 接口
     */
    private void initService(Set<Class<?>> apiDocServices) {
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
            INTERFACE_CACHE.put(serviceInfo, interfaceInfos);

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
