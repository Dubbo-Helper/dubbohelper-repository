package com.dubbohelper.admin.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by zhangxiaoman on 2018/11/14.
 */
@Getter
@Setter
@Component
public class Config {

    @Value("${dubbo.registry.address}")
    private String dubboUrl;

    @Value("${maven.repository.url}")
    private String mavenRepositoryUrl;
}
