package com.dubbohelper.demo.api.dto;

import com.dubbohelper.common.annotations.ApidocElement;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BaseResponse implements Serializable {
    @ApidocElement(value = "会话ID")
    private String sid;
    @ApidocElement(value = "处理结果")
    private boolean success;
    @ApidocElement(value = "处理结果代码")
    private String code;
    @ApidocElement(value = "处理结果描述")
    private String description;
}
