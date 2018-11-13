package com.dubbohelper.admin.template;

import com.dubbohelper.admin.apidoc.InterfaceInfo;
import com.dubbohelper.admin.apidoc.ServiceInfo;
import com.dubbohelper.admin.elementInfo.ElementInfo;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class ApiTemplateExtractor {

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
    public void buildDocBody(Model model, List<ServiceInfo> serviceList, List<InterfaceInfo> interfaceList,
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
        model.addAttribute("serviceList", serviceList);
        model.addAttribute("interfaceList", interfaceList);
        model.addAttribute("currentService", currentService);
        model.addAttribute("currentMethod", currentMethod);
        model.addAttribute("usage", usage);
        model.addAttribute("requestClass", requestClass);
        model.addAttribute("requestContent", requestContent);
        model.addAttribute("responseClass", responseClass);
        model.addAttribute("responseContent", responseContent);
    }
}
