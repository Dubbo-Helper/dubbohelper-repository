package com.dubbohelper.admin.service;

import com.dubbohelper.admin.dto.MavenCoordDTO;

import java.util.Set;

/**
 * Created by zhangxiaoman on 2018/11/15.
 */
public interface RegisterService {


    /**
     * 搜索应用
     * @param keyWord
     * @return
     */
    Set<MavenCoordDTO> search(String keyWord);

    /**
     * 断开zk连接
     * @throws Exception
     */
    void disconnect();

    /**
     * 重新连接zk
     * @param zkUrl
     */
    void reConnection(String zkUrl) throws Exception;
}
