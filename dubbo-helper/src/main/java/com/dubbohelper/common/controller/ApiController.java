package com.dubbohelper.common.controller;

import com.dubbohelper.common.dto.result.BaseResult;
import com.dubbohelper.common.dto.PostRequest;
import com.dubbohelper.common.dto.result.ResultDTO;
import com.dubbohelper.common.enums.ResultCodeEnum;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 测一测功能controller
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Slf4j
@Controller
@RequestMapping({"api"})
public class ApiController implements ApplicationContextAware {
    private final Map<String, Class<?>> localCacheMap = new HashMap<String, Class<?>>();
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").disableHtmlEscaping().serializeNulls().create();

    @ResponseBody
    @RequestMapping({"/{service}/{method}"})
    public Object invoke(@ModelAttribute PostRequest postRequest,
                         @PathVariable("service") String service,
                         @PathVariable("method") String method) {

        ResultDTO resultDTO = new ResultDTO();
        BaseResult result = new BaseResult();
        postRequest.setService(service);
        postRequest.setMethod(method);

        String fullServiceName = postRequest.getService();
        // 从缓存中取类
        Class<?> serviceClz = localCacheMap.get(fullServiceName);
        if (serviceClz == null) {
            try {
                serviceClz = Class.forName(fullServiceName);
            } catch (ClassNotFoundException e) {
                log.error("class not found!", e);
                result.setSuccess(Boolean.FALSE);
                result.setCode(ResultCodeEnum.FAIL.getCode());
                result.setDescription("service not found," + postRequest.getService());
                return gson.toJson(result);
            }
        }
        Object serviceBean = null;
        //获取Spring中注册的Bean
        try {
            serviceBean = this.ctx.getBean(serviceClz);
        } catch (BeansException e) {
            log.error("bean not found!", e);
            result.setSuccess(Boolean.FALSE);
            result.setCode(ResultCodeEnum.FAIL.getCode());
            result.setDescription("bean not found," + postRequest.getService());
            return gson.toJson(result);
        }
        Method[] methods = serviceClz.getMethods();
        Method methodReflect = null;
        for (Method m : methods) {
            if (m.getName().equals(postRequest.getMethod())) {
                methodReflect = m;
                break;
            }
        }
        if (methodReflect == null) {
            result.setSuccess(Boolean.FALSE);
            result.setCode(ResultCodeEnum.FAIL.getCode());
            result.setDescription("method not found" + postRequest.getMethod());
            return gson.toJson(result);
        }
        // 支持泛型
        Type[] paramTypes = methodReflect.getGenericParameterTypes();
        Object[] inputParam = new Object[0];
        if (paramTypes.length == 1) {
            inputParam = new Object[]{gson.fromJson(postRequest.getParam().replaceAll("&quot;","\""), paramTypes[0])};
        } else {
            result.setSuccess(Boolean.FALSE);
            result.setCode(ResultCodeEnum.FAIL.getCode());
            result.setDescription("parameter is error," + postRequest.getMethod());
            return gson.toJson(result);
        }
        try {
            Object callResult = methodReflect.invoke(serviceBean, inputParam);
            return gson.toJson(callResult);
        } catch (InvocationTargetException e) {
            log.error("call service error:", e);
            resultDTO.setSuccess(Boolean.FALSE);
            resultDTO.setCode(ResultCodeEnum.FAIL.getCode());
            resultDTO.setDescription("call service error," + postRequest.getService());
        } catch (IllegalArgumentException e) {
            log.error("illegal argument error:", e);
            resultDTO.setSuccess(Boolean.FALSE);
            resultDTO.setCode(ResultCodeEnum.PARAM_FAIL.getCode());
            resultDTO.setDescription("call service illegal argument error," + postRequest.getService());
        } catch (Exception e) {
            log.error("unkown error:", e);
            resultDTO.setSuccess(Boolean.FALSE);
            resultDTO.setCode(ResultCodeEnum.FAIL.getCode());
            resultDTO.setDescription("call service unkown error," + postRequest.getService());
        }finally {
            // 如果没有加入缓存，加入缓存
            if (localCacheMap.get(fullServiceName) == null) {
                localCacheMap.put(fullServiceName, serviceClz);
            }
        }

        return gson.toJson(resultDTO);
    }

    private ApplicationContext ctx;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
