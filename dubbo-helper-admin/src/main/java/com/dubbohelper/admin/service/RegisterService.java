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


}
