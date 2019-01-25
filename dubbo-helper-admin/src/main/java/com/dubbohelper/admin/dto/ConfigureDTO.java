package com.dubbohelper.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigureDTO {

    /**
     * Zookeeper连接地址
     */
    private String zkAddress;

    /**
     * maven仓库地址
     */
    private String repositoryUrl;
}
