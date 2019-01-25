package com.dubbohelper.admin.common.enums;

import java.io.File;

public enum FilePathEnum {
    /**
     * 已加载jar列表文档
     */
    JARINFOS(".dubbohelper/db/jarInfos.txt", "已加载jar列表文档"),
    /**
     * 系统配置文件
     */
    CONFIGURE(".dubbohelper/config/config.json", "系统配置文件"),
    /**
     * jar包存放路径
     */
    JARPATH(".dubbohelper/jars/", "jar包存放路径(需填完整路径)"),
    /**
     * 临时文件存放路径
     */
    TMEP(".dubbohelper/temp/", "临时文件存放路径");

    /**
     * 相对路径
     */
    private String relativePath;

    private String desc;

    FilePathEnum(String relativePath, String desc) {
        this.relativePath = relativePath;
        this.desc = desc;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 获取绝对路径
     *
     * @return 绝对路径
     */
    public String getAbsolutePath() {
        return System.getProperty("user.home") + File.separator + getRelativePath();
    }

}
