package com.dubbohelper.common.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 测一测request
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Getter
@Setter
public class PostRequest {

    private String service;

    private String method;

    private String param;
}
