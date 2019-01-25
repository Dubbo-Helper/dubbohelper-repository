package com.dubbohelper.demo.api.service;

import com.dubbohelper.common.annotations.ApidocInterface;
import com.dubbohelper.common.annotations.ApidocService;
import com.dubbohelper.demo.api.dto.user.UserRequest;
import com.dubbohelper.demo.api.dto.user.UserResponse;


/**
 *  demo
 *  @title 用户服务
 *  @author chengcy
 *  @date 2019/01/24
 *  @since 1.0.0
 */
@ApidocService("用户服务")
public interface UserService {

    /**
     * 添加用户
     * @title 添加用户
     */
    @ApidocInterface("添加用户")
    UserResponse addUser(UserRequest request);

}
