package com.dubbohelper.admin.controller;

import com.dubbohelper.admin.dto.SearchAppResDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaoman on 2018/12/10.
 */
@RestController
public class DocController {

    @RequestMapping("/search/{keyWord}")
    public List<SearchAppResDTO> search(@PathVariable String keyWord) {
        List<SearchAppResDTO> list = new ArrayList<>();
        SearchAppResDTO dto1 = SearchAppResDTO.builder()
                .appName("java-jinrong-cashloan-web")
                .groupId("com.zbj.finance.cashloan")
                .artifactId("java-jinrong-cashloan-api")
                .build();
        boolean add = list.add(dto1);

        SearchAppResDTO dto2 = SearchAppResDTO.builder()
                .appName("java-jinrong-cashloan-web")
                .groupId("com.zbj.finance.cashloan")
                .artifactId("java-jinrong-cashloanmgmt-api")
                .build();
        list.add(dto2);

        SearchAppResDTO dto3 = SearchAppResDTO.builder()
                .appName("java-jinrong-memcenter-dubbo")
                .groupId("com.zbj.finance.member")
                .artifactId("java-jinrong-memcenter-api")
                .build();
        list.add(dto3);
        return list;
    }
}
