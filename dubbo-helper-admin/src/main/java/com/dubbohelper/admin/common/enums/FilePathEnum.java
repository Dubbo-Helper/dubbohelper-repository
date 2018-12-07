package com.dubbohelper.admin.common.enums;

public enum FilePathEnum {
    /**
     * 已加载jar列表文档
     */
    JARINFOS(".dubbohelper/db/jarInfos.txt","已加载jar列表文档"),
    /**
     * 系统配置文件
     */
    CONFIGURE(".dubbohelper/config/configure.txt","系统配置文件"),
    /**
     * jar包存放路径(需填完整路径)
     */
    JARPATH(".dubbohelper/jars/","jar包存放路径(需填完整路径)");

    private String path;

    private String desc;

    FilePathEnum(String path, String desc) {
        this.path = path;
        this.desc = desc;
    }

    public String getPath() {
        return path;
    }

    public String getDesc() {
        return desc;
    }
}
