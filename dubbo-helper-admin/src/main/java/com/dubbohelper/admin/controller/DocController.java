package com.dubbohelper.admin.controller;

import com.dubbohelper.admin.dto.SearchAppResDTO;
import com.dubbohelper.admin.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by zhangxiaoman on 2018/12/10.
 */
@RestController
public class DocController {
    @Autowired
    RegisterService registerService;

    @RequestMapping("/search/{keyWord}")
    public List<SearchAppResDTO> search(@PathVariable String keyWord) {
        return registerService.search(keyWord);
    }
}
