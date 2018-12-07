package com.dubbohelper.admin.service;

import com.dubbohelper.admin.dto.Application;

import java.util.List;

/**
 * Created by zhangxiaoman on 2018/11/15.
 */
public interface RegisterService {


    /**
     * 搜索应用
     * @param keyWord
     * @return
     */
    List<Application> search(String keyWord);


}
