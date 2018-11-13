package com.dubbohelper.admin.converter;

import com.dubbohelper.admin.apidoc.ExtendField;
import com.dubbohelper.admin.apidoc.ParamInfo;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParamConverter {

    public String getData(HttpServletRequest request, List<ExtendField> extendFieldList){

        Map<String,String> paramMap = getParamMap(request,extendFieldList);

        Map<String,Object> jsonMap = new HashMap<String, Object>();
        for (Map.Entry<String,String> entry :paramMap.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            makeData(jsonMap,key,value);
        }
        handleListNull(jsonMap);
        return new Gson().toJson(jsonMap);
    }

    public ParamInfo parseJsonToParam(HttpServletRequest request, String cookieKey){

        ParamInfo paramInfo = new ParamInfo();
        String service = request.getParameter("service");
        String method = request.getParameter("method");

        String requestJson = StringEscapeUtils.unescapeHtml(request.getParameter("requestJson"));
        if(StringUtils.isEmpty(requestJson)){
            //从cookie获取
            Cookie[] cookies = request.getCookies();
            if(cookies != null){
                for (Cookie cookie : cookies){
                    try {
                        if(cookie.getName().equals(cookieKey+service+"."+method)){
                            requestJson = URLDecoder.decode(cookie.getValue().toString(), "UTF-8");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(StringUtils.isEmpty(requestJson)){
            return paramInfo;
        }

        requestJson = asciiToNative(requestJson);
        requestJson = requestJson.replace("\\","");

        try {

            Map<String,Object> jsonMap = new Gson().fromJson(requestJson, Map.class);
            parse(paramInfo,jsonMap,"");
        }catch (Exception e){
            e.printStackTrace();
        }
        return paramInfo;
    }

    private void handleListNull(Map<String,Object> jsonMap){
        for (Map.Entry<String,Object> entry :jsonMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof  List){
                List<Map<String,Object>> list = (List<Map<String, Object>>) value;
                List<Map<String,Object>> newList = new ArrayList<Map<String,Object>>();
                for (Map<String,Object> map : list){
                    if(map == null){
                        continue;
                    }
                    newList.add(map);
                }
                jsonMap.put(key,newList);
            }
        }
    }

    private void makeData(Map<String,Object> jsonMap,String key,String value){
        if(!key.contains(".")){
            jsonMap.put(key,value);
        }else{
            String currentKey = key.substring(0,key.indexOf("."));
            String subKey = key.substring(key.indexOf(".")+1,key.length());

            Integer currentIndex = null;
            if(currentKey.contains("[")){
                currentIndex = Integer.valueOf(currentKey.substring(currentKey.indexOf("[")+1,currentKey.indexOf("]")));
                currentKey = currentKey.substring(0,currentKey.indexOf("["));
            }

            Map<String,Object> map = null;
            if(currentIndex != null){
                List<Map<String,Object>> list = null;
                if(jsonMap.containsKey(currentKey)){
                    list = (List<Map<String, Object>>) jsonMap.get(currentKey);
                    if(list == null){
                        list = new ArrayList<Map<String,Object>>();
                        jsonMap.put(currentKey,list);
                    }
                }else{
                    list = new ArrayList<Map<String,Object>>();
                    jsonMap.put(currentKey,list);
                }

                if(list.size() <= currentIndex){
                    for (int i = list.size();i<currentIndex;i++){
                        list.add(null);
                    }
                    map = new HashMap<String,Object>();
                    list.add(map);
                }else{
                    map = list.get(currentIndex);
                    if(map == null){
                        map = new HashMap<String,Object>();
                        list.set(currentIndex,map);
                    }
                }
            }else{
                if(jsonMap.containsKey(currentKey)){
                    map = (Map<String, Object>) jsonMap.get(currentKey);
                }else{
                    map = new HashMap<String,Object>();
                    jsonMap.put(currentKey,map);
                }
            }
            makeData(map, subKey, value);
        }
    }

    private Map<String,String> getParamMap(HttpServletRequest request, List<ExtendField> extendFieldList){
        //参数转换
        Map<String,String> paramMap = new LinkedHashMap<String,String>();
        if(request.getParameterMap() != null && request.getParameterMap().size()>0){
            for (Map.Entry<String, String[]>  entry : request.getParameterMap().entrySet()){
                String key = entry.getKey();
                if("requestUrl".equals(key) || "service".equals(key) || "method".equals(key)){
                    continue;
                }
                if(!CollectionUtils.isEmpty(extendFieldList)){
                    boolean ignoreFiled = false;
                    for (ExtendField extendField : extendFieldList){
                        if(extendField.getFieldName().equals(key)){
                            ignoreFiled = true;
                        }
                    }
                    if(ignoreFiled){
                        continue;
                    }
                }

                String[] values = entry.getValue();
                String value = null;
                if(values != null && values.length > 0){
                    value = values[0];
                    if(StringUtils.isEmpty(value)){
                        continue;
                    }
                }
                paramMap.put(key,value);
            }
        }
        return  paramMap;
    }

    private void parse(ParamInfo paramInfo,Map<String,Object> jsonMap ,String name){
        String originalName = name;
        for (Map.Entry<String,Object> entry : jsonMap.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof Map){
                Map<String,Object> map = (Map<String, Object>) value;
                name = originalName+key+".";
                parse(paramInfo,map,name);
            }else if(value instanceof  List){
                List<Map<String,Object>> list = (List<Map<String, Object>>) value;
                if(!CollectionUtils.isEmpty(list)){
                    paramInfo.getListInfo().put(key,list.size());
                    if(paramInfo.getMaxSize() < list.size()){
                        paramInfo.setMaxSize(list.size());
                    }
                    for (int i=0;i<list.size();i++){
                        Map<String,Object> map = list.get(i);
                        name = originalName+key+"["+i+"].";
                        parse(paramInfo,map,name);
                    }
                }
            }else{
                if(value != null){
                    name = originalName+key;
                    paramInfo.getParams().put(name, value.toString());
                }
            }
        }
    }

    private String asciiToNative (String asciicode) {
        String[] asciis = asciicode.split ("\\\\u");
        String nativeValue = asciis[0];
        try {
            for ( int i = 1; i < asciis.length; i++ ) {
                String code = asciis[i];
                nativeValue += (char) Integer.parseInt (code.substring (0, 4), 16);
                if (code.length () > 4) {
                    nativeValue += code.substring (4, code.length ());
                }
            }
        } catch (NumberFormatException e) {
            return asciicode;
        }
        return nativeValue;
    }
}
