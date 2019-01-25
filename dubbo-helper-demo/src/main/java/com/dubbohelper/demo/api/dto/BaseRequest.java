package com.dubbohelper.demo.api.dto;

import com.dubbohelper.common.annotations.ApidocElement;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BaseRequest implements Serializable {
    @ApidocElement(value = "会话ID")
    private String sid;

    @ApidocElement(value = "交易编码")
    private String transCode;

    @ApidocElement(value = "渠道编号")
    private String channelNo;
}
