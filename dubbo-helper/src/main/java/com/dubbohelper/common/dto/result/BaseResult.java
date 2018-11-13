package com.dubbohelper.common.dto.result;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * BaseResult
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Getter
@Setter
public class BaseResult implements Serializable {

    /** 请求标识号 */
    private String sid;

    /** 成功标志 */
    private boolean success;

    /** 信息码 */
    private String code;

    /** 描述 */
    private String description;
}
