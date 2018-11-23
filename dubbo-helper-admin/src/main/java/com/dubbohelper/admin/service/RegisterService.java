package com.dubbohelper.admin.service;

import com.dubbohelper.admin.dto.Application;

import java.util.List;

/**
 * Created by zhangxiaoman on 2018/11/15.
 */
public interface RegisterService {
    /**
     * 我的收藏
     * @param ip
     * @return
     */
    List<String> myCollect(String ip);

    /**
     * 搜索应用
     * @param keyWord
     * @return
     */
    List<Application> search(String keyWord);

    /**
     * 收藏应用
     * @param appName
     */
    void mark(String ip, String appName);
}
