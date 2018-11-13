package com.dubbohelper.admin.apidoc;

import com.dubbohelper.admin.elementInfo.ElementInfo;
import com.dubbohelper.admin.util.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ApiDocScanner {
    private final Map<ServiceInfo, List<InterfaceInfo>> INTERFACE_CACHE = new ConcurrentHashMap<ServiceInfo, List<InterfaceInfo>>();

    @SneakyThrows
    public void downloadApiDoc(String fileName, OutputStream outputStream) throws FileNotFoundException {
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
            if (serviceInfo.className.equals(className)) {
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
            if (serviceInfo.className.equals(className)) {
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

    public synchronized void init(boolean ifThrow, String...docScanPackages) {
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

        initService(ifThrow,apiDocServices);
    }

    private void initService(boolean ifThrow,Set<Class<?>> apiDocServices) {
        for (Class<?> service : apiDocServices) {
            if (!service.isInterface()) {
                log.warn("{} is not interface", service);
                continue;
            }

            String usage0 = "";
            String value = "";
            Annotation[] serviceAnnotations = service.getAnnotations();
            Map<String,String> serviceAnnotationMap = new HashMap<>();
            if (serviceAnnotations.length > 0) {
                for (Annotation annotation:serviceAnnotations) {
                    if (annotation.annotationType().getSimpleName().equals("ApidocService")) {
                        serviceAnnotationMap = getAnnotationDetail(annotation,"ApidocService");
                        if (!StringUtils.isEmpty(serviceAnnotationMap.get("usage"))) {
                            usage0 = serviceAnnotationMap.get("usage");
                        }
                        if (!StringUtils.isEmpty(serviceAnnotationMap.get("value"))) {
                            value = serviceAnnotationMap.get("value");
                        }
                    }
                }
            }

            ServiceInfo serviceInfo = new ServiceInfo(value, service.getName(), usage0);
            List<InterfaceInfo> interfaceInfos = new ArrayList<InterfaceInfo>();
            INTERFACE_CACHE.put(serviceInfo, interfaceInfos);

            Method[] methods = service.getMethods();
            for (Method m : methods) {
                String name = service.getName() + "." + m.getName();
                String usage = usage0;
                String desc = "";
                Annotation[] annotations = m.getAnnotations();
                if (annotations.length > 0) {
                    for (Annotation annotation:annotations) {
                        if (annotation.annotationType().getSimpleName().equals("ApidocInterface")) {
                            Map<String,String> interfaceAnnotationMap = getAnnotationDetail(annotation, "ApidocInterface");
                            desc = interfaceAnnotationMap.get("value");
                            if (!StringUtils.isEmpty(interfaceAnnotationMap.get("usage"))) {
                                usage = interfaceAnnotationMap.get("usage");
                            }
                        }
                    }
                }

                InterfaceInfo interfaceInfo = new InterfaceInfo(name,desc, usage, service.getName(), m.getName(),"","");
                interfaceInfos.add(interfaceInfo);
                //加载接口请求、响应
                Class requestClazz = null;
                if (m.getParameterTypes().length > 0) {
                    if (m.getParameterTypes().length == 1) {
                        requestClazz = m.getParameterTypes()[0];
                        interfaceInfo.setRequestName(requestClazz.getName());
                    } else {
                        log.warn("{}.{} 存在过多参数", service.getName(), m.getName());
                        break;
                    }
                }
                Class returnClazz = m.getReturnType();
                Set<String> responseSet = new HashSet<String>();
                responseSet.add("com.dubbohelper.common.dto.result.ItemResultDTO");
                responseSet.add("com.dubbohelper.common.dto.result.ListResultDTO");
                responseSet.add("com.dubbohelper.common.dto.result.page.PaginationResultDTO");
                responseSet.add("com.zhubajie.finance.common.dto.PaginationResultDTO");
                responseSet.add("com.zhubajie.finance.common.dto.ItemResultDTO");
                responseSet.add("com.zhubajie.finance.common.dto.ListResultDTO");


                if (responseSet.contains(returnClazz.getName())) {
                    Class[] classes = null;
                    try {
                        classes = extractInterfaceMethodReturn(m);
                        if (classes.length != 1) {
                            log.error("{}.{} 返回值泛型信息不为一个", service.getName(), m.getName());
                            break;
                        }
                        returnClazz = classes[0];
                        interfaceInfo.setResponseName(returnClazz.getName());
                    } catch (Exception e) {
                        if (ifThrow) {

                        }
                    }
                }

                if("com.dubbohelper.common.dto.result.ResultDTO".equals(returnClazz.getName())){
                    returnClazz = null;
                }

                BeanExtractor extractor = new BeanExtractor();
                if (requestClazz != null) {
                    List<ElementInfo> requests = extractor.extract(requestClazz);
                    interfaceInfo.getRequest().addAll(requests);
                }
                if (returnClazz != null) {
                    List<ElementInfo> responses = extractor.extract(returnClazz);
                    interfaceInfo.getResponse().addAll(responses);
                }
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

    private Map<String,String> getAnnotationDetail(Annotation annotation,String type) {
        int beginLength = annotation.toString().indexOf(type);
        int endLength = annotation.toString().length();
        String data1 = annotation.toString().substring(beginLength + type.length() + 1,endLength-1);
        String[] data2 = data1.split(",");
        Map<String, String> map = new HashMap<>();
        for (String data3:data2) {
            String[] data4 = data3.split("=");
            if (data4.length == 2) {
                map.put(data4[0].trim(),data4[1].trim());
            }
        }

        return map;
    }

}
