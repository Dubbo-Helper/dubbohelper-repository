package com.dubbohelper.demo.api.dto.user;

import com.dubbohelper.common.annotations.ApidocElement;
import com.dubbohelper.demo.api.dto.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户Request
 */
@Getter
@Setter
public class UserRequest extends BaseRequest implements Serializable {

    @ApidocElement(value = "用户ID")
    private String userId;

    @ApidocElement(value = "用户名",required = false)
    private String username;

}
