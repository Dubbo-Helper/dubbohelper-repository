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
     * maven创建地址
     */
    private String repositoryPath;
}
