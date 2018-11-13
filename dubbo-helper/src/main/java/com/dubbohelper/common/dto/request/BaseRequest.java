package com.dubbohelper.common.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * BaseRequest
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Getter
@Setter
public class BaseRequest implements Serializable {

    /**
     * 请求标识号
     */
    private String sid;
}
