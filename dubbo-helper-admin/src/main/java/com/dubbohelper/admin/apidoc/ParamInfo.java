package com.dubbohelper.admin.apidoc;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ParamInfo {

    private Map<String,Integer> listInfo = new HashMap<String, Integer>();
    private Map<String,String> params = new HashMap<String, String>();
    private Integer maxSize = 1;
}
