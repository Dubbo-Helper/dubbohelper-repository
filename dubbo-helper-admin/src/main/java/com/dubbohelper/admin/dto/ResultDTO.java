package com.dubbohelper.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResultDTO<T> implements Serializable {

    /**
     * 成功标志
     */
    private Boolean success;

    /**
     * 信息码
     */
    private String code;

    /**
     * 描述
     */
    private String description;

    /**
     * 数据
     */
    private T data;
}
