package com.dubbohelper.admin.util;

import com.dubbohelper.admin.scanner.InterfaceInfo;
import com.dubbohelper.admin.scanner.ServiceInfo;
import com.dubbohelper.admin.scanner.elementInfo.ElementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 组装视图model工具
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Slf4j
public class ModelUtil {

    /**
     * 填充model
     *
     * @param model
     * @param serviceList
     * @param interfaceList
     * @param currentInterfaceInfo
     * @param requestContent
     * @param responseContent
     * @throws Exception
     */
    public static void getModel(ModelAndView model, List<ServiceInfo> serviceList, List<InterfaceInfo> interfaceList,
                             InterfaceInfo currentInterfaceInfo, List<ElementInfo> requestContent, List<ElementInfo> responseContent) throws Exception{

        String currentService = "";
        if(!StringUtils.isEmpty(currentInterfaceInfo.getClassName())){
            currentService = currentInterfaceInfo.getClassName();
        }
        String currentMethod = "";
        if(!StringUtils.isEmpty(currentInterfaceInfo.getMethodName())){
            currentMethod = currentInterfaceInfo.getMethodName();
        }
        String usage = "";
        if(!StringUtils.isEmpty(currentInterfaceInfo.getUsage())){
            usage = currentInterfaceInfo.getUsage();
        }

        String requestClass = "";
        String responseClass = "";
        if(!StringUtils.isEmpty(currentService) && !StringUtils.isEmpty(currentMethod)){
            Class<?> clazz = Class.forName(currentService);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods){
                if(method.getName().equals(currentMethod)){

                    Type type = method.getGenericParameterTypes()[0];
                    requestClass = type.toString();
                    requestClass = requestClass.replace("class ","").replace("<", "&lt;").replace(">","&gt;");

                    type = method.getGenericReturnType();
                    responseClass = type.toString();
                    responseClass = responseClass.replace("class ","").replace("<","&lt;").replace(">","&gt;");
                    break;
                }
            }
        }

        // 设置变量
        model.addObject("serviceList", serviceList);
        model.addObject("interfaceList", interfaceList);
        model.addObject("currentService", currentService);
        model.addObject("currentMethod", currentMethod);
        model.addObject("usage", usage);
        model.addObject("requestClass", requestClass);
        model.addObject("requestContent", requestContent);
        model.addObject("responseClass", responseClass);
        model.addObject("responseContent", responseContent);
    }
}
